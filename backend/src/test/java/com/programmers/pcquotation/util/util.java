package com.programmers.pcquotation.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmers.pcquotation.domain.customer.entity.Customer;
import com.programmers.pcquotation.domain.customer.service.CustomerService;
import com.programmers.pcquotation.domain.seller.entitiy.Seller;
import com.programmers.pcquotation.domain.seller.service.SellerService;
@ActiveProfiles("test")
public class util {
	public static Seller registerSeller(String username, String password, MockMvc mvc, SellerService sellerService) throws Exception {
		ResultActions resultActions = mvc
			.perform(post("/api/auth/signup/seller")
				.content(String.format("""
					{
					    "username": "%s",
					    "password": "%s",
					    "confirmPassword": "%s",
					    "companyName": "너6구리",
					    "email": "%s@gmail.com",
					    "verificationQuestion": "바나나는",
					    "verificationAnswer": "길어"
					}
					""".stripIndent(), username, password,password, username))
				.contentType(
					new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
				)
			)
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.message").value("회원가입 성공"))
			.andDo(print());
		Optional<Seller> sellers = sellerService.findByUserName(username);
		assertNotNull(sellers.get());
		return sellers.get();
	}

	public static String loginSeller(String username, String password,MockMvc mvc, SellerService sellerService) throws Exception {
		registerSeller(username,password, mvc,sellerService);
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
			.andExpect(jsonPath("$.message").value("로그인 성공"))
			.andExpect(status().isOk());
		String responseJson = resultActions.andReturn().getResponse().getContentAsString();

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(responseJson);
		return jsonNode.get("apiKey").asText() + " " + jsonNode.get("accessToken").asText() + " " + jsonNode.get("userType").asText();
	}

	public static Customer registerCustomer(String username, String password, MockMvc mvc,
		CustomerService customerService) throws Exception {
		ResultActions resultActions = mvc
			.perform(post("/api/auth/signup/customer")
				.content(String.format("""
					{
					    "username": "%s",
					    "password": "%s",
					    "confirmPassword": "%s",
					    "companyName": "너구5리",
					    "email": "%s@gmaidl.com",
					    "verificationQuestion": "바나나는",
					    "verificationAnswer": "길어"
					}
					""".stripIndent(), username, password,password, username))
				.contentType(
					new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
				)
			)
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.message").value("회원가입 성공"));
		Optional<Customer> customer = customerService.findCustomerByUsername(username);
		assertNotNull(customer.get());
		return customer.get();
	}

	public static String loginCustomer(String username, String password, MockMvc mvc, CustomerService customerService) throws Exception {
		registerCustomer(username,password, mvc,customerService);
		ResultActions resultActions = mvc
			.perform(post("/api/auth/login/customer")
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
			.andExpect(jsonPath("$.message").value("로그인 성공"))
			.andExpect(status().isOk());
		String responseJson = resultActions.andReturn().getResponse().getContentAsString();

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(responseJson);
		return jsonNode.get("apiKey").asText() + " " + jsonNode.get("accessToken").asText() + " " + jsonNode.get("userType").asText();
	}

}
