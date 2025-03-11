package com.programmers.pcquotation.domain.member.controller

import com.programmers.pcquotation.domain.customer.dto.CustomerSignupRequest
import com.programmers.pcquotation.domain.customer.dto.CustomerSignupResponse
import com.programmers.pcquotation.domain.member.dto.*
import com.programmers.pcquotation.domain.member.service.AuthService
import com.programmers.pcquotation.domain.member.service.OAuth2Service
import com.programmers.pcquotation.domain.seller.dto.SellerSignupRequest
import com.programmers.pcquotation.domain.seller.dto.SellerSignupResponse
import com.programmers.pcquotation.global.enums.UserType
import org.springframework.http.HttpHeaders.LOCATION
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val oAuth2Service: OAuth2Service
) {

    @PostMapping("/signup/customer")
    fun signup(@RequestBody customerSignupRequest: CustomerSignupRequest): ResponseEntity<CustomerSignupResponse> {
        val signupResponse = authService.processCustomerSignup(customerSignupRequest)
        return ResponseEntity(signupResponse, HttpStatus.CREATED)
    }

    @PostMapping("/signup/seller")
    fun signup(@RequestBody sellerSignupRequest: SellerSignupRequest): ResponseEntity<SellerSignupResponse> {
        val signupResponse = authService.processSellerSignup(sellerSignupRequest)
        return ResponseEntity(signupResponse, HttpStatus.CREATED)
    }

    @PostMapping("/login/customer")
    fun loginCustomer(@RequestBody customerLoginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        val loginResponse = authService.processLoginCustomer(customerLoginRequest)
        return ResponseEntity(loginResponse, HttpStatus.OK)
    }

    @PostMapping("/login/seller")
    fun loginSeller(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        val loginResponse = authService.processLoginSeller(loginRequest)
        return ResponseEntity(loginResponse, HttpStatus.OK)
    }

    @PostMapping("/login/admin")
    fun loginAdmin(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        val loginResponse = authService.processLoginAdmin(loginRequest)
        return ResponseEntity(loginResponse, HttpStatus.OK)
    }

    @GetMapping("/login/kakao/{userType}")
    fun kakaoLogin(
        @RequestParam(required = false, defaultValue = "") code: String,
        @RequestParam(required = false, defaultValue = "") error: String,
        @RequestParam(required = false, defaultValue = "") error_description: String,
        @RequestParam(required = false, defaultValue = "") state: String,
        @PathVariable userType: String,
    ): ResponseEntity<*> {
        val kakaoReqDto: OAuth2AuthInfoDto = OAuth2AuthInfoDto(
            code,
            error,
            state,
            UserType.fromString(userType)
        )
        oAuth2Service.kakaoLogin(kakaoReqDto)

        return ResponseEntity.status(HttpStatus.FOUND)
            .header(LOCATION, "http://localhost:3000/social/login")
            .build<Any>()
    }

    @GetMapping("/login/naver/{userType}")
    fun naverLogin(
        @RequestParam(required = false, defaultValue = "") code: String,
        @RequestParam(required = false, defaultValue = "") error: String,
        @RequestParam(required = false, defaultValue = "") error_description: String,
        @RequestParam(required = false, defaultValue = "") state: String,
        @PathVariable userType: String,
    ): ResponseEntity<*> {
        val naverReqDto = OAuth2AuthInfoDto(
            code,
            error,
            state,
            UserType.fromString(userType)
        )

        oAuth2Service.naverLogin(naverReqDto)

        // 4. 홈페이지로 리다이렉트
        return ResponseEntity.status(HttpStatus.FOUND)
            .header(LOCATION, "http://localhost:3000/social/login")
            .build<Any>()
    }

    @GetMapping
    fun CheckAuthentication(): ResponseEntity<AuthRequest> {

        val authRequest = authService.memberFromRq
        return ResponseEntity(authRequest, HttpStatus.OK)
    }

    @PostMapping("/logout")
    fun logout(): ResponseEntity<Void> {
        authService.processLogout()
        return ResponseEntity(HttpStatus.OK)
    }
}