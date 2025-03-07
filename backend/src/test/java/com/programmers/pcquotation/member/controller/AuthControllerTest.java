package com.programmers.pcquotation.member.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.pcquotation.domain.customer.service.CustomerService;
import com.programmers.pcquotation.domain.seller.service.SellerService;
import com.programmers.pcquotation.util.util;
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerTest {
	@Autowired
	private MockMvc mvc;
	@Autowired
	private SellerService sellerService;
	@Autowired
	private CustomerService customerService;
	String customerId = "customer123456";
	String sellerId = "seller12345";

	String ps = "password1234";

	@Test
	@Transactional
	@DisplayName("회원 회원가입")
	void t1() throws Exception {
		util.registerCustomer(customerId,ps,mvc,customerService);
	}

	@Test
	@Transactional
	@DisplayName("판매자 회원가입")
	void t2() throws Exception {
		util.registerSeller(sellerId,ps,mvc,sellerService);
	}

	@Test
	@DisplayName("JWT 회원 로그인")
	@WithMockUser(username = "test1234", roles = {"CUSTOMER"}) //  SecurityContext 강제설정?
	void t3() throws Exception {
		String responseBody = util.loginCustomer(customerId,ps,mvc,customerService);
		String[] parts = responseBody.split(" ");
		assertEquals(3, parts.length, "응답 형식이 올바르지 않음");
		assertFalse(parts[0].isEmpty(), "JWT 토큰이 비어 있음");
		assertFalse(parts[1].isEmpty(), "API 키가 비어 있음");
		assertFalse(parts[2].isEmpty(), "회원 유형이 비어 있음");

	}
	@Test
	@DisplayName("JWT 판매자 로그인")
	@WithMockUser(username = "test1234", roles = {"SELLER"}) //  SecurityContext 강제설정?
	void t4() throws Exception {
		String responseBody =  util.loginSeller(sellerId,ps,mvc,sellerService);
		String[] parts = responseBody.split(" ");
		assertEquals(3, parts.length, "응답 형식이 올바르지 않음");
		assertFalse(parts[0].isEmpty(), "JWT 토큰이 비어 있음");
		assertFalse(parts[1].isEmpty(), "API 키가 비어 있음");
		assertFalse(parts[2].isEmpty(), "회원 유형이 비어 있음");
	}
}