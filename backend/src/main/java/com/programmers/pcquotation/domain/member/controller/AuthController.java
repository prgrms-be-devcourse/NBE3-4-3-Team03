package com.programmers.pcquotation.domain.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.pcquotation.domain.customer.dto.CustomerSignupRequest;
import com.programmers.pcquotation.domain.customer.dto.CustomerSignupResponse;
import com.programmers.pcquotation.domain.member.dto.AuthRequest;
import com.programmers.pcquotation.domain.member.entitiy.Member;
import com.programmers.pcquotation.domain.member.service.AuthService;
import com.programmers.pcquotation.domain.member.dto.LoginRequest;
import com.programmers.pcquotation.domain.member.dto.LoginResponse;
import com.programmers.pcquotation.domain.seller.dto.SellerSignupRequest;
import com.programmers.pcquotation.domain.seller.dto.SellerSignupResponse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup/customer")
    public ResponseEntity<CustomerSignupResponse> signup(@RequestBody CustomerSignupRequest customerSignupRequest) {
        CustomerSignupResponse signupResponse = authService.processSignup(customerSignupRequest);
        return new ResponseEntity<>(signupResponse, HttpStatus.CREATED);
    }

	@PostMapping("/signup/seller")
	public ResponseEntity<SellerSignupResponse> signup(@RequestBody SellerSignupRequest sellerSignupRequest) {
		SellerSignupResponse signupResponse = authService.processSignup(sellerSignupRequest);
		return new ResponseEntity<>(signupResponse, HttpStatus.CREATED);
	}

	@PostMapping("/login/customer")
	public ResponseEntity<LoginResponse> loginCustomer(@RequestBody LoginRequest customerLoginRequest) {
        LoginResponse loginResponse = authService.processLoginCustomer(customerLoginRequest);
		return new ResponseEntity<>(loginResponse, HttpStatus.OK);
	}

	@PostMapping("/login/seller")
	public ResponseEntity<LoginResponse> loginSeller(@RequestBody LoginRequest loginRequest) {
		LoginResponse loginResponse = authService.processLoginSeller(loginRequest);
		return new ResponseEntity<>(loginResponse, HttpStatus.OK);
	}
	@PostMapping("/login/admin")
	public ResponseEntity<LoginResponse> loginAdmin(@RequestBody LoginRequest loginRequest) {
		LoginResponse loginResponse = authService.processLoginAdmin(loginRequest);
		return new ResponseEntity<>(loginResponse, HttpStatus.OK);
	}
	@GetMapping
	public ResponseEntity<AuthRequest> CheckAuthentication(){
		AuthRequest authRequest = authService.getMemberFromRq();
		return new ResponseEntity<>(authRequest, HttpStatus.OK);
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout() {
		authService.processLogout();
		return new ResponseEntity<>(HttpStatus.OK);
	}
}