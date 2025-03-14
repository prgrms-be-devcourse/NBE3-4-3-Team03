package com.programmers.pcquotation.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.programmers.pcquotation.domain.customer.entity.Customer
import com.programmers.pcquotation.domain.customer.service.CustomerService
import com.programmers.pcquotation.domain.seller.entity.Seller
import com.programmers.pcquotation.domain.seller.service.SellerService
import org.junit.jupiter.api.Assertions
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.nio.charset.StandardCharsets

@ActiveProfiles("test")
object Util {
    @Throws(Exception::class)
    fun registerSeller(username: String, password: String?, mvc: MockMvc, sellerService: SellerService): Seller {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/auth/signup/seller")
                    .content(
                        String.format(
                            """
					{
					    "username": "%s",
					    "password": "%s",
					    "confirmPassword": "%s",
					    "companyName": "너6구리",
					    "email": "%s@gmail.com",
					    "verificationQuestion": "바나나는",
					    "verificationAnswer": "길어"
					}
					
					""".trimIndent(), username, password, password, username
                        )
                    )
                    .contentType(
                        MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                    )
            )
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("회원가입 성공"))
            .andDo(MockMvcResultHandlers.print())
        val sellers = sellerService.findByUserName(username)
        Assertions.assertNotNull(sellers.get())
        return sellers.get()
    }

    @Throws(Exception::class)
    fun loginSeller(username: String, password: String?, mvc: MockMvc, sellerService: SellerService): String {
        registerSeller(username, password, mvc, sellerService)
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/auth/login/seller")
                    .content(
                        String.format(
                            """
					{
					    "username": "%s",
					    "password": "%s"
					}
					
					""".trimIndent(), username, password
                        )
                    )
                    .contentType(
                        MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                    )
            )
            .andDo(MockMvcResultHandlers.print())
        resultActions
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("로그인 성공"))
            .andExpect(MockMvcResultMatchers.status().isOk())
        val responseJson = resultActions.andReturn().response.contentAsString

        val objectMapper = ObjectMapper()
        val jsonNode = objectMapper.readTree(responseJson)
        return jsonNode["apiKey"].asText() + " " + jsonNode["accessToken"].asText() + " " + jsonNode["userType"].asText()
    }

    @Throws(Exception::class)
    fun registerCustomer(
        username: String?, password: String?, mvc: MockMvc,
        customerService: CustomerService
    ): Customer {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/auth/signup/customer")
                    .content(
                        String.format(
                            """
					{
					    "username": "%s",
					    "password": "%s",
					    "confirmPassword": "%s",
					    "companyName": "너구5리",
					    "email": "%s@gmaidl.com",
					    "verificationQuestion": "바나나는",
					    "verificationAnswer": "길어"
					}
					
					""".trimIndent(), username, password, password, username
                        )
                    )
                    .contentType(
                        MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                    )
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("회원가입 성공"))
        val customer = customerService.findCustomerByUsername(username)
        Assertions.assertNotNull(customer.get())
        return customer.get()
    }

    @Throws(Exception::class)
    fun loginCustomer(username: String?, password: String?, mvc: MockMvc, customerService: CustomerService): String {
        registerCustomer(username, password, mvc, customerService)
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/auth/login/customer")
                    .content(
                        String.format(
                            """
					{
					    "username": "%s",
					    "password": "%s"
					}
					
					""".trimIndent(), username, password
                        )
                    )
                    .contentType(
                        MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                    )
            )
            .andDo(MockMvcResultHandlers.print())
        resultActions
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("로그인 성공"))
            .andExpect(MockMvcResultMatchers.status().isOk())
        val responseJson = resultActions.andReturn().response.contentAsString

        val objectMapper = ObjectMapper()
        val jsonNode = objectMapper.readTree(responseJson)
        return jsonNode["apiKey"].asText() + " " + jsonNode["accessToken"].asText() + " " + jsonNode["userType"].asText()
    }
}