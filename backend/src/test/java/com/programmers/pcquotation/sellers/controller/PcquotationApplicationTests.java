package com.programmers.pcquotation.sellers.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.pcquotation.domain.member.service.AuthService;
import com.programmers.pcquotation.domain.seller.entitiy.Seller;
import com.programmers.pcquotation.domain.seller.service.SellerService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class PcquotationApplicationTests {
	@Autowired
	SellerService sellerService;

	@Autowired
	AuthService authService;

	@Autowired
	private MockMvc mvc;

	String id = "test1234";
	String ps = "password1234";

	Seller register() throws Exception {
		ResultActions resultActions = mvc
			.perform(post("/api/auth/signup/seller")
				.content(String.format("""
					{
					    "username": "%s",
					    "password": "%s",
					    "confirmPassword": "password1234",
					    "companyName": "너구리",
					    "email": "abc@gmail.com",
					    "verificationQuestion": "바나나는",
					    "verificationAnswer": "길어"
					}
					""".stripIndent(), id, ps))
				.contentType(
					new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
				)
			)
			.andDo(print());
		Optional<Seller> sellers = sellerService.findByUserName("test1234");
		assertNotNull(sellers.get());
		return sellers.get();
	}

	String login(String username,String password) throws Exception {
		register();
		ResultActions resultActions = mvc
			.perform(post("/api/auth/login/seller")
				.content(String.format("""
					{
					    "username": "%s",
					    "password": "%s"
					}
					""".stripIndent(), username, password))
				.contentType(
					new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
				)
			)
			.andDo(print());
		resultActions
			.andExpect(status().isOk());
		String responseJson = resultActions.andReturn().getResponse().getContentAsString();

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(responseJson);
		return jsonNode.get("apiKey").asText() + " " + jsonNode.get("accessToken").asText() + " "+ jsonNode.get("userType").asText();
	}


	@Test
	@Transactional
	@WithMockUser(username = "test1234", roles = {"SELLER"}) //  SecurityContext 강제설정?
	@DisplayName("사업자 번호 조회")
	void t2() throws Exception {
		String token = login(id,ps);
		ResultActions resultActions1 = mvc
			.perform(get("/seller/business/2208183676/check")
				.header("Authorization", "Bearer " + token)
				.contentType(
					new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
				)
			)
			.andDo(print());
		resultActions1
			.andExpect(handler().methodName("checkCode"))
			.andExpect(status().isOk())
			.andExpect(content().string("true"));
		ResultActions resultActions2 = mvc
			.perform(get("/seller/business/220818/check")
				.contentType(
					new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
				)
			)
			.andDo(print());
		resultActions2
			.andExpect(status().isOk())
			.andExpect(content().string("false"));
	}

	@Test
	@DisplayName("JWT 로그인 구현")
	void t3() throws Exception {
		register();
		ResultActions resultActions = mvc
			.perform(post("/api/auth/login/seller")
				.content(String.format("""
					{
					    "username": "%s",
					    "password": "%s"
					}
					""".stripIndent(), id, ps))
				.contentType(
					new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
				)
			)
			.andDo(print());

		resultActions
			.andExpect(status().isOk());

		String responseBody = resultActions.andReturn().getResponse().getContentAsString();

		String[] parts = responseBody.split(" ");
		assertEquals(2, parts.length, "응답 형식이 올바르지 않음");
		assertFalse(parts[0].isEmpty(), "JWT 토큰이 비어 있음");
		assertFalse(parts[1].isEmpty(), "API 키가 비어 있음");
	}

	@Test
	@Transactional
	@WithMockUser(username = "test1234", roles = {"SELLER"}) //  SecurityContext 강제설정?
	@DisplayName("판매자 정보 조회")
	void t4() throws Exception {
		String token = login(id,ps);
		ResultActions resultActions1 = mvc
			.perform(get("/seller")
				.header("Authorization", "Bearer " + token)
				.contentType(
					new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
				)
			)
			.andDo(print());
		resultActions1
			.andExpect(handler().methodName("info"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").isNotEmpty())
			.andExpect(jsonPath("$.username").isNotEmpty())
			.andExpect(jsonPath("$.companyName").isNotEmpty())
			.andExpect(jsonPath("$.email").isNotEmpty());

	}

	@Test
	@Transactional
	@WithMockUser(username = "test1234", roles = {"SELLER"}) //  SecurityContext 강제설정?
	@DisplayName("판매자 정보 수정")
	void t5() throws Exception {
		String token = login(id,ps);
		String username = "zzzzzzz";
		String password = ps;
		String companyName = "sdasdaasdasd";
		String email ="aaaa@naver.com";
		String newPassword = "zzzzzzzzzz";
		String confirmNewPassword = "zzzzzzzzzz";;

		ResultActions resultActions1 = mvc
			.perform(put("/seller")
				.header("Authorization", "Bearer " + token)
				.content(String.format("""
					{
						"username": "%s",
					    "password": "%s",
					    "companyName": "%s",
					    "email": "%s",
					    "newPassword": "%s",
					    "confirmNewPassword": "%s"
					    
					}
					""".stripIndent(), username,password,companyName,email,newPassword,confirmNewPassword))
				.contentType(
					new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
				)
			)
			.andDo(print());
		resultActions1
			.andExpect(handler().methodName("modify"))
			.andExpect(status().isOk())
			.andExpect(content().string("정보수정이 성공했습니다."));

	}



}