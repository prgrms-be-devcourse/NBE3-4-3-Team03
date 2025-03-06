package com.programmers.pcquotation.domain.member.controller

import com.programmers.pcquotation.domain.customer.dto.CustomerSignupRequest
import com.programmers.pcquotation.domain.customer.dto.CustomerSignupResponse
import com.programmers.pcquotation.domain.member.dto.AuthRequest
import com.programmers.pcquotation.domain.member.dto.LoginRequest
import com.programmers.pcquotation.domain.member.dto.LoginResponse
import com.programmers.pcquotation.domain.member.service.AuthService
import com.programmers.pcquotation.domain.seller.dto.SellerSignupRequest
import com.programmers.pcquotation.domain.seller.dto.SellerSignupResponse
import lombok.RequiredArgsConstructor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
class AuthController (
    private val authService: AuthService
){

    @PostMapping("/signup/customer")
    fun signup(@RequestBody customerSignupRequest: CustomerSignupRequest): ResponseEntity<CustomerSignupResponse> {
        val signupResponse = authService.processSignup(customerSignupRequest)
            return ResponseEntity(signupResponse, HttpStatus.CREATED)
    }

    @PostMapping("/signup/seller")
    fun signup(@RequestBody sellerSignupRequest: SellerSignupRequest): ResponseEntity<SellerSignupResponse> {
        val signupResponse = authService.processSignup(sellerSignupRequest)
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