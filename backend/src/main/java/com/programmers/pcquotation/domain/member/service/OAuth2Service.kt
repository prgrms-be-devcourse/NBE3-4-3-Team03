package com.programmers.pcquotation.domain.member.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.programmers.pcquotation.domain.member.dto.OAuth2AuthInfoDto
import com.programmers.pcquotation.domain.member.dto.OAuth2KakaoUserInfoDto
import com.programmers.pcquotation.domain.member.dto.OAuth2NaverUserInfoDto
import com.programmers.pcquotation.domain.member.dto.OAuth2TokenInfoDto
import com.programmers.pcquotation.domain.customer.service.CustomerService
import com.programmers.pcquotation.domain.member.exception.Oauth2InfoResponseException
import com.programmers.pcquotation.domain.member.exception.Oauth2LoginException
import com.programmers.pcquotation.domain.member.exception.RedirectInfoException
import com.programmers.pcquotation.domain.member.exception.TokenNotFoundException
import com.programmers.pcquotation.domain.member.exception.UserNotFoundException
import com.programmers.pcquotation.global.properties.OAuth2ProviderProperties
import com.programmers.pcquotation.global.properties.OAuth2RegistrationProperties
import com.programmers.pcquotation.global.config.RestTemplateConfig
import com.programmers.pcquotation.global.enums.UserType
import com.programmers.pcquotation.global.enums.UserType.*
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.http.HttpMethod.POST
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.exchange

@Service
class OAuth2Service(
    private val restTemplateConfig: RestTemplateConfig,
    private val oAuthRegistration: OAuth2RegistrationProperties,
    private val oAuthProvider: OAuth2ProviderProperties,
    private val authService: AuthService,
    private val customerService: CustomerService,
) {


    fun defaultHeader(): HttpHeaders {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8")
        return headers
    }

    fun kakaoLogin(authInfo: OAuth2AuthInfoDto) {
        if (authInfo.code.isEmpty()) {
            throw RedirectInfoException("Redirect information does not exist")
        }
        val kakaoTokenInfo = this.getKakaoTokenInfo(authInfo.code, authInfo.userType)
        val userInfo = this.getKakaoUserInfo(kakaoTokenInfo.accessToken)
        try {
            when (authInfo.userType) {
                CUSTOMER -> {
                    val customer = customerService.findCustomerByEmail(userInfo.response.email)
                    if (customer == null)
                        authService.processCustomerSignup(userInfo.toCustomerSignupRequest())
                    authService.processLoginCustomer(userInfo.toLoginRequest())
                }

                else -> {
                    throw Oauth2LoginException("login_failed")
                }
            }
        } catch (e: RuntimeException) {
            throw Oauth2LoginException("login_failed")
        }
    }

    private fun cvtObjectToMap(obj: Any): Map<String, Any> {
        val mapper = ObjectMapper()
        return mapper.convertValue(obj, object : TypeReference<Map<String, Any>>() {
        })
    }


    private fun getKakaoTokenInfo(authCode: String, userType: UserType): OAuth2TokenInfoDto {

        var resultDto: OAuth2TokenInfoDto? = null
        val responseTokenInfo: ResponseEntity<Map<String, Any>>?
        val requestParamMap: MultiValueMap<String, Any> = LinkedMultiValueMap()
        requestParamMap.apply {
            add("grant_type", "authorization_code")
            add("client_id", oAuthRegistration.kakao.clientId)
            add("redirect_uri", oAuthRegistration.kakao.redirectUri + "/" + userType.value.lowercase())
            add("code", authCode)
            add("client_secret", oAuthRegistration.kakao.clientSecret)
        }
        val requestMap: HttpEntity<MultiValueMap<String, Any>> = HttpEntity(requestParamMap, this.defaultHeader())
        try {
            responseTokenInfo = restTemplateConfig
                .restTemplate()
                .exchange<Map<String, Any>>(
                    oAuthProvider.kakao.tokenUri, POST, requestMap,
                    object : ParameterizedTypeReference<Map<String?, Any?>?>() {
                    })
        } catch (e: java.lang.Exception) {
            throw Oauth2InfoResponseException(e.message)
        }
        if (responseTokenInfo.body != null && responseTokenInfo.statusCode.is2xxSuccessful
        ) {
            val body = responseTokenInfo.body
            if (body != null) {
                resultDto = OAuth2TokenInfoDto(
                    body["access_token"].toString(),
                    body["refresh_token"].toString(),
                    body["token_type"].toString()
                )
            }
        }
        return resultDto ?: throw TokenNotFoundException("Token information does not exist")
    }

    private fun getKakaoUserInfo(accessToken: String): OAuth2KakaoUserInfoDto {
        var responseUserInfo: ResponseEntity<Map<String, Any>>?
        var resultDto: OAuth2KakaoUserInfoDto? = null
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.apply {
            add("Content-type", "application/x-www-form-urlencoded;charset=utf-8")
            add("Authorization", "Bearer $accessToken")
        }
        val userInfoParam: MultiValueMap<String, Any> = LinkedMultiValueMap()
        val userInfoReq: HttpEntity<MultiValueMap<String, Any>> = HttpEntity(userInfoParam, headers)
        try {
            responseUserInfo = restTemplateConfig
                .restTemplate()
                .exchange(
                    oAuthProvider.kakao.userInfoUri, POST, userInfoReq,
                    object : ParameterizedTypeReference<Map<String, Any>>() {
                    })
        } catch (e: java.lang.Exception) {
            throw Oauth2InfoResponseException(e.message)
        }
        if (responseUserInfo?.body != null && responseUserInfo.statusCode
                .is2xxSuccessful
        ) {
            val body = responseUserInfo.body
            if (body != null) {
                val kakaoAccount = body["kakao_account"]?.let { this.cvtObjectToMap(it) }
                val profile =
                    body["kakao_account"]
                        ?.let { it -> this.cvtObjectToMap(it)["profile"]?.let { this.cvtObjectToMap(it) } }
                resultDto = OAuth2KakaoUserInfoDto(
                    body["resultcode"].toString(),
                    body["message"].toString(),
                    OAuth2KakaoUserInfoDto.KakaoUserResponse(
                        body["id"].toString(),
                        profile?.get("nickname").toString(),
                        kakaoAccount?.get("email").toString(),
                        profile?.get("nickname").toString()
                    )
                )
            }
        }
        return resultDto ?: throw UserNotFoundException("User information does not exist")
    }

    fun naverLogin(authInfo: OAuth2AuthInfoDto) {

        if (authInfo.code.isEmpty()) {
            println("Redirect information does not exist")
        }
        val naverTokenInfo = this.getNaverTokenInfo(authInfo.code, authInfo.state, authInfo.userType)
        val accessToken = naverTokenInfo.accessToken
        val userInfo = this.getNaverUserInfo(accessToken)
        try {
            when (authInfo.userType) {
                CUSTOMER -> {
                    val customer = customerService.findCustomerByEmail(userInfo.response.email)
                    if (customer != null)
                        authService.processCustomerSignup(userInfo.toCustomerSignupRequest())
                    authService.processLoginCustomer(userInfo.toLoginRequest())
                }

                else -> {
                    throw Oauth2LoginException("login_failed")
                }
            }
        } catch (e: RuntimeException) {
            throw Oauth2LoginException("login_failed")
        }
    }

    private fun getNaverTokenInfo(authCode: String, state: String, userType: UserType): OAuth2TokenInfoDto {
        var resultDto: OAuth2TokenInfoDto? = null
        var responseTokenInfo: ResponseEntity<Map<String, Any>>?
        val requestParamMap: MultiValueMap<String, String> = LinkedMultiValueMap()
        requestParamMap.apply {
            add("grant_type", "authorization_code")
            add("client_id", oAuthRegistration.naver.clientId)
            add("client_secret", oAuthRegistration.naver.clientSecret)
            add("code", authCode)
            add("state", state)
            requestParamMap.add("redirect_uri", oAuthRegistration.naver.redirectUri + "/" + userType.value.lowercase())
        }
        val requestMap: HttpEntity<MultiValueMap<String, String>> = HttpEntity(requestParamMap, this.defaultHeader())
        try {
            responseTokenInfo = restTemplateConfig
                .restTemplate()
                .exchange(
                    oAuthProvider.naver.tokenUri,
                    HttpMethod.POST,
                    requestMap,
                    object : ParameterizedTypeReference<Map<String, Any>>() {
                    })
        } catch (e: java.lang.Exception) {
            throw Oauth2InfoResponseException(e.message)
        }
        if (responseTokenInfo?.body != null && responseTokenInfo.statusCode
                .is2xxSuccessful
        ) {
            val body = responseTokenInfo.body
            if (body != null) {
                resultDto = OAuth2TokenInfoDto(
                    body["access_token"].toString(),
                    body["refresh_token"].toString(),
                    body["token_type"].toString()
                )
            }
        }

        return resultDto ?: throw TokenNotFoundException("Token information does not exist")
    }

    private fun getNaverUserInfo(accessToken: String): OAuth2NaverUserInfoDto {
        var responseUserInfo: ResponseEntity<Map<String, Any>>?
        var resultDto: OAuth2NaverUserInfoDto? = null

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.add("Authorization", "Bearer $accessToken")
        val userInfoParam = LinkedMultiValueMap<String, Any>()
        val userInfoReq = HttpEntity(userInfoParam, headers)
        try {
            responseUserInfo = restTemplateConfig
                .restTemplate()
                .exchange(
                    oAuthProvider.naver.userInfoUri,
                    HttpMethod.POST,
                    userInfoReq,
                    object : ParameterizedTypeReference<Map<String, Any>>() {
                    })
        } catch (e: java.lang.Exception) {
            throw Oauth2InfoResponseException(e.message)
        }
        if (responseUserInfo.body != null && responseUserInfo.statusCode.is2xxSuccessful
        ) {
            val body = responseUserInfo.body
            if (body?.get("response") != null) {
                val response = body["response"]
                val resBody = response?.let { this.cvtObjectToMap(it) }
                if (resBody == null) throw Exception()
                resultDto = OAuth2NaverUserInfoDto(
                    body["resultcode"].toString(),
                    body["message"].toString(),
                    OAuth2NaverUserInfoDto.NaverUserResponse(
                        resBody["id"].toString(),
                        resBody["nickname"].toString(),
                        resBody["email"].toString(),
                        resBody["name"].toString(),
                    )
                )
            }
        }
        return resultDto ?: throw UserNotFoundException("User information does not exist")
    }
}