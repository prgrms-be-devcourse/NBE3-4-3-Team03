package com.programmers.pcquotation.domain.member.service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.programmers.pcquotation.domain.admin.entitiy.Admin;
import com.programmers.pcquotation.domain.admin.service.AdminService;
import com.programmers.pcquotation.domain.customer.dto.CustomerSignupRequest;
import com.programmers.pcquotation.domain.customer.dto.CustomerSignupResponse;
import com.programmers.pcquotation.domain.customer.entity.Customer;
import com.programmers.pcquotation.domain.customer.exception.CustomerAlreadyExistException;
import com.programmers.pcquotation.domain.customer.exception.IncorrectLoginAttemptException;
import com.programmers.pcquotation.domain.customer.exception.PasswordMismatchException;
import com.programmers.pcquotation.domain.customer.service.CustomerService;
import com.programmers.pcquotation.domain.member.dto.AuthRequest;
import com.programmers.pcquotation.domain.member.entitiy.Member;
import com.programmers.pcquotation.domain.member.dto.LoginRequest;
import com.programmers.pcquotation.domain.member.dto.LoginResponse;
import com.programmers.pcquotation.domain.seller.dto.SellerSignupRequest;
import com.programmers.pcquotation.domain.seller.dto.SellerSignupResponse;
import com.programmers.pcquotation.domain.seller.entitiy.Seller;
import com.programmers.pcquotation.domain.seller.service.SellerService;
import com.programmers.pcquotation.global.enums.UserType;
import com.programmers.pcquotation.global.jwt.Jwt;
import com.programmers.pcquotation.global.rq.Rq;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthService {
	@Value("${jwt.secretKey}")
	private String jwtSecretKey;

	@Value("${jwt.accessToken.expirationSeconds}")
	private long accessTokenExpirationSeconds;

	private final CustomerService customerService;
	private final SellerService sellerService;
	private final AdminService adminService;
	private final PasswordEncoder passwordEncoder;
	private final Rq rq;

	public CustomerSignupResponse processSignup(CustomerSignupRequest customerSignupRequest) {
		if (!customerSignupRequest.getPassword().equals(customerSignupRequest.getConfirmPassword())) {
			throw new PasswordMismatchException();
		}

		if (customerService.findCustomerByUsername(customerSignupRequest.getUsername()).isPresent()) {
			throw new CustomerAlreadyExistException();
		}

		if (customerService.findCustomerByEmail(customerSignupRequest.getEmail()).isPresent()) {
			throw new CustomerAlreadyExistException();
		}

		Customer customer = customerSignupRequest.toCustomer();
		customer.setApiKey(UUID.randomUUID().toString());
		customer.setPassword(passwordEncoder.encode(customer.getPassword()));
		customerService.createCustomer(customer);

		return new CustomerSignupResponse(customer, "회원가입 성공");
	}

	public SellerSignupResponse processSignup(SellerSignupRequest sellerSignupRequest) {
		if (!sellerSignupRequest.getPassword().equals(sellerSignupRequest.getConfirmPassword())) {
			throw new PasswordMismatchException();
		}

		if (sellerService.findSellerByUsername(sellerSignupRequest.getUsername()).isPresent()) {
			throw new CustomerAlreadyExistException();
		}

		if (sellerService.findSellerByEmail(sellerSignupRequest.getEmail()).isPresent()) {
			throw new CustomerAlreadyExistException();
		}

		Seller seller = sellerSignupRequest.toSeller();
		seller.setApiKey(UUID.randomUUID().toString());
		seller.setPassword(passwordEncoder.encode(seller.getPassword()));
		sellerService.createSeller(seller);

		return new SellerSignupResponse(seller, "회원가입 성공");
	}


	public LoginResponse processLoginCustomer(LoginRequest customerLoginRequest) {
		String username = customerLoginRequest.getUsername();
		Customer customer = customerService.findCustomerByUsername(username)
			.orElseThrow(IncorrectLoginAttemptException::new);

		if (!passwordEncoder.matches(customerLoginRequest.getPassword(), customer.getPassword())) {
			throw new IncorrectLoginAttemptException();
		}

		String accessToken = this.getAccessToken(customer);
		rq.setCookie("accessToken", accessToken);
		rq.setCookie("apiKey", customer.getApiKey());
		rq.setCookie("userType", UserType.CUSTOMER.toString());
		return LoginResponse.builder()
			.apiKey(customer.getApiKey())
			.accessToken(accessToken)
			.userType(UserType.SELLER)
			.message("로그인 성공")
			.build();
	}

	public LoginResponse processLoginSeller(LoginRequest loginRequest) {
		String username = loginRequest.getUsername();
		Seller seller = sellerService.findByUserName(username)
			.orElseThrow(IncorrectLoginAttemptException::new);

		if (!passwordEncoder.matches(loginRequest.getPassword(), seller.getPassword())) {
			throw new IncorrectLoginAttemptException();
		}
		String accessToken = this.getAccessToken(seller);
		rq.setCookie("accessToken", accessToken);
		rq.setCookie("apiKey", seller.getApiKey());
		rq.setCookie("userType", UserType.SELLER.toString());

		return LoginResponse.builder()
			.apiKey(seller.getApiKey())
			.accessToken(accessToken)
			.userType(UserType.SELLER)
			.message("로그인 성공")
			.build();
	}
	public LoginResponse processLoginAdmin(LoginRequest loginRequest) {
		String username = loginRequest.getUsername();
		Admin admin = adminService.findAdminByUsername(username)
			.orElseThrow(IncorrectLoginAttemptException::new);
		if (!passwordEncoder.matches(loginRequest.getPassword(), admin.getPassword())) {
			throw new IncorrectLoginAttemptException();
		}
		String accessToken = this.getAccessToken(admin);
		rq.setCookie("accessToken", accessToken);
		rq.setCookie("apiKey", admin.getApiKey());
		rq.setCookie("userType", UserType.ADMIN.toString());

		return LoginResponse.builder()
			.apiKey(admin.getApiKey())
			.accessToken(accessToken)
			.userType(UserType.ADMIN)
			.message("로그인 성공")
			.build();
	}
	public AuthRequest getMemberFromRq(){
		Member member = rq.getMember();
		String auth = member.getAuthorities().toString();
		String userType =  UserType.fromString(auth).toString();
		return AuthRequest
				.builder()
				.userType(userType)
				.build();
	}
	public Member getMemberFromAccessToken(String accessToken,UserType userType) {
		Map<String, Object> payload = this.payload(accessToken);

		if(payload == null) return null;

		long id = (long) payload.get("id");
		switch (userType){
			case SELLER -> {
				return sellerService.findById(id).orElse(null);
			}
			case CUSTOMER -> {
				return customerService.findById(id).orElse(null);
			}
			case ADMIN -> {
				return adminService.findById(id).orElse(null);
			}
		}
		return null;
	}

	public String getAccessToken(Member member) {
		long id = member.getId();
		String username = member.getUsername();
		return Jwt.INSTANCE.toString(
			jwtSecretKey,
			accessTokenExpirationSeconds,
			Map.of("id", id, "username", username)
		);
	}

	public Optional<Member> findByApiKey(String apiKey, UserType userType) {
		switch (userType){
			case CUSTOMER -> {
				return customerService.findByApiKey(apiKey);
			}
			case SELLER -> {
				return sellerService.findByApiKey(apiKey);
			}
			case ADMIN -> {
				return adminService.findByApiKey(apiKey);
			}
		}

		return Optional.empty();
	}

	Map<String, Object> payload(String accessToken) {
		Map<String, Object> parsedPayload = Jwt.INSTANCE.payload(jwtSecretKey, accessToken);

		if (parsedPayload == null)
			return null;

		long id = (long)(Integer)parsedPayload.get("id");
		String username = (String)parsedPayload.get("username");

		return Map.of("id", id, "username", username);
	}

	public void processLogout() {
		rq.deleteCookie("accessToken");
		rq.deleteCookie("apiKey");
		rq.deleteCookie("userType");
	}
}