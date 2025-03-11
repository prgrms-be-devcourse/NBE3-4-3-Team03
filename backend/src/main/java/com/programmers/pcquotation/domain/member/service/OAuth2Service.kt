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
import com.programmers.pcquotation.global.Properties.OAuth2ProviderProperties
import com.programmers.pcquotation.global.Properties.OAuth2RegistrationProperties
import com.programmers.pcquotation.global.config.RestTemplateConfig
import com.programmers.pcquotation.global.enums.UserType
import com.programmers.pcquotation.global.enums.UserType.*
import com.programmers.pcquotation.global.rq.Rq
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.http.HttpMethod.POST
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.exchange


@Slf4j
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
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED;
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        return headers;
    }

    @Throws(RuntimeException::class)
    fun kakaoLogin(authInfo: OAuth2AuthInfoDto) {
        if (authInfo.code.isEmpty()) {
            throw RedirectInfoException("Redirect information does not exist")
        }
        val kakaoTokenInfo = this.getKakaoTokenInfo(authInfo.code, authInfo.userType);
        val userInfo = this.getKakaoUserInfo(kakaoTokenInfo.accessToken)
        try {
            when (authInfo.userType) {
                CUSTOMER -> {
                    val customer = customerService.findCustomerByEmail(userInfo.response.email)
                    if (customer.isEmpty)
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
        return mapper.convertValue<Map<String, Any>>(obj, object : TypeReference<Map<String, Any>>() {
        })
    }

    @Throws(RuntimeException::class)
    private fun getKakaoTokenInfo(authCode: String, userType: UserType): OAuth2TokenInfoDto {

        var resultDto: OAuth2TokenInfoDto? = null
        var responseTokenInfo: ResponseEntity<Map<String, Any>>? = null

        // [STEP1] 카카오 토큰 URL로 전송할 데이터 구성
        val requestParamMap: MultiValueMap<String, Any> = LinkedMultiValueMap()
        requestParamMap.add("grant_type", "authorization_code");
        requestParamMap.add("client_id", oAuthRegistration.kakao.clientId);
        requestParamMap.add("redirect_uri", oAuthRegistration.kakao.redirectUri + "/" + userType.value.lowercase());
        requestParamMap.add("code", authCode);
        requestParamMap.add("client_secret", oAuthRegistration.kakao.clientSecret);
        val requestMap: HttpEntity<MultiValueMap<String, Any>> = HttpEntity(requestParamMap, this.defaultHeader())
        try {
            // [STEP2] 카카오 토큰 URL로 RestTemplate 이용하여 데이터 전송
            responseTokenInfo = restTemplateConfig
                .restTemplate()
                .exchange<Map<String, Any>>(
                    oAuthProvider.kakao.tokenUri, POST, requestMap,
                    object : ParameterizedTypeReference<Map<String?, Any?>?>() {
                    })
        } catch (e: java.lang.Exception) {
            throw Oauth2InfoResponseException(e.message)
        }

        // [STEP3] 토큰 반환 값 결과값으로 구성
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

    @Throws(RuntimeException::class)
    private fun getKakaoUserInfo(accessToken: String): OAuth2KakaoUserInfoDto {
        var responseUserInfo: ResponseEntity<Map<String, Any>>? = null
        var resultDto: OAuth2KakaoUserInfoDto? = null

        // [STEP1] 필수 요청 Header 값 구성 : ContentType, Authorization
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8")
        headers.add("Authorization", "Bearer $accessToken") // accessToken 추가
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
        // [STEP4] 사용자 정보가 존재한다면 값을 불러와서 OAuth2KakaoUserInfoDto 객체로 구성하여 반환합니다.
        if (responseUserInfo?.body != null && responseUserInfo.statusCode
                .is2xxSuccessful
        ) {

            val body = responseUserInfo.body
            if (body != null) {
                val kakaoAccount = body["kakao_account"]?.let { this.cvtObjectToMap(it) }
                val profile =
                    body["kakao_account"]
                        ?.let { this.cvtObjectToMap(it)["profile"]?.let { this.cvtObjectToMap(it) } };

                resultDto = OAuth2KakaoUserInfoDto(
                    body["resultcode"].toString(),
                    body["message"].toString(),
                    OAuth2KakaoUserInfoDto.KakaoUserResponse(
                        body["id"].toString(), // 사용자 아이디 번호
                        profile?.get("nickname").toString(),
                        kakaoAccount?.get("email").toString(), // 이메일
                        profile?.get("nickname").toString()
                    )

                )
            }
        }
        return resultDto ?: throw UserNotFoundException("User information does not exist")
    }

    @Throws(RuntimeException::class)
    fun naverLogin(authInfo: OAuth2AuthInfoDto) {
        var resultUserInfo: OAuth2NaverUserInfoDto? = null


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
                    if (customer.isEmpty)
                        authService.processCustomerSignup(userInfo.toCustomerSignupRequest())
                    authService.processLoginCustomer(userInfo.toLoginRequest())
                }

                SELLER -> TODO()
                ADMIN -> TODO()
                NOTHING -> TODO()
            }
        } catch (e: RuntimeException) {
            throw Oauth2LoginException("login_failed")
        }
    }

    @Throws(RuntimeException::class)
    private fun getNaverTokenInfo(authCode: String, state: String, userType: UserType): OAuth2TokenInfoDto {
        var resultDto: OAuth2TokenInfoDto? = null
        var responseTokenInfo: ResponseEntity<Map<String, Any>>? = null
        val requestParamMap: MultiValueMap<String, String> = LinkedMultiValueMap()
        requestParamMap.add(
            "grant_type",
            "authorization_code"
        ) // 인증 과정에 대한 구분값: 1. 발급:'authorization_code', 2. 갱신:'refresh_token', 3. 삭제: 'delete'
        requestParamMap.add("client_id", oAuthRegistration.naver.clientId) // 애플리케이션 등록 시 발급받은 Client ID 값
        requestParamMap.add(
            "client_secret",
            oAuthRegistration.naver.clientSecret
        ) // 애플리케이션 등록 시 발급받은 Client secret 값
        requestParamMap.add("code", authCode) // 로그인 인증 요청 API 호출에 성공하고 리턴받은 인증코드값 (authorization code)
        requestParamMap.add(
            "state",
            state
        ) // 사이트 간 요청 위조(cross-site request forgery) 공격을 방지하기 위해 애플리케이션에서 생성한 상태 토큰값으로 URL 인코딩을 적용한 값을 사용
        requestParamMap.add(
            "redirect_uri",
            oAuthRegistration.naver.redirectUri + "/" + userType.value.lowercase()
        ) // 애플리케이션 등록 시 발급받은 Client secret 값
        val requestMap: HttpEntity<MultiValueMap<String, String>> = HttpEntity(requestParamMap, this.defaultHeader())

        try {
            // [STEP2] 네이버 토큰 URL로 RestTemplate 이용하여 데이터 전송
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

        // [STEP3] 토큰 반환 값 결과값으로 구성
        if (responseTokenInfo?.body != null && responseTokenInfo.statusCode
                .is2xxSuccessful
        ) {
            val body = responseTokenInfo.body

            if (body != null) {
                resultDto = OAuth2TokenInfoDto(
                    body["access_token"].toString(),
                    body["refresh_token"].toString(),
                    body["token_type"].toString(),
                    body["expires_in"].toString()
                )
            }
        }

        return resultDto ?: throw TokenNotFoundException("Token information does not exist")
    }

    @Throws(RuntimeException::class)
    private fun getNaverUserInfo(accessToken: String): OAuth2NaverUserInfoDto {
        var responseUserInfo: ResponseEntity<Map<String, Any>>? = null
        var resultDto: OAuth2NaverUserInfoDto? = null

        // [STEP1] 필수 요청 Header 값 구성 : ContentType, Authorization
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.add("Authorization", "Bearer $accessToken") // accessToekn 추가
        val userInfoParam = LinkedMultiValueMap<String, Any>()
        val userInfoReq = HttpEntity(userInfoParam, headers)


        // [STEP3] 요청 Header, 파라미터를 포함하여 사용자 정보 조회 URL로 요청을 수행합니다.
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


        // [STEP4] 사용자 정보가 존재한다면 값을 불러와서 OAuth2NaverUserInfoDto 객체로 구성하여 반환합니다.
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
