package com.programmers.pcquotation.domain.seller.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.programmers.pcquotation.domain.member.service.AuthService
import com.programmers.pcquotation.domain.seller.entity.Seller
import com.programmers.pcquotation.domain.seller.service.SellerService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.StandardCharsets

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
internal class SellerControllerTest {
    @Autowired
    private lateinit var sellerService: SellerService

    @Autowired
    private lateinit var authService: AuthService

    @Autowired
    private lateinit var mvc: MockMvc

    private val id: String = "test1234"
    private val ps: String = "password1234"

    @Throws(Exception::class)
    fun register(): Seller {
        val resultActions = mvc
            .perform(
                MockMvcRequestBuilders.post("/api/auth/signup/seller")
                    .content(
                        String.format(
                            """
					{
					    "username": "%s",
					    "password": "%s",
					    "confirmPassword": "password1234",
					    "companyName": "너구리",
					    "email": "abc@gmail.com",
					    "verificationQuestion": "바나나는",
					    "verificationAnswer": "길어"
					}
					
					""".trimIndent(), id, ps
                        )
                    )
                    .contentType(
                        MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                    )
            )
            .andDo(MockMvcResultHandlers.print())
        val sellers = sellerService.findByUserName("test1234")
        Assertions.assertNotNull(sellers.get())
        return sellers.get()
    }

    @Throws(Exception::class)
    fun login(username: String?, password: String?): String {
        register()
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
            .andExpect(MockMvcResultMatchers.status().isOk())
        val responseJson = resultActions.andReturn().response.contentAsString

        val objectMapper = ObjectMapper()
        val jsonNode = objectMapper.readTree(responseJson)
        return jsonNode["apiKey"].asText() + " " + jsonNode["accessToken"].asText() + " " + jsonNode["userType"].asText()
    }


    @Test
    @Transactional
    @WithMockUser(username = "test1234", roles = ["SELLER"]) //  SecurityContext 강제설정?
    @DisplayName("사업자 번호 조회")
    @Throws(Exception::class)
    fun t2() {
        val token = login(id, ps)
        val resultActions1 = mvc
            .perform(
                MockMvcRequestBuilders.get("/seller/business/2208183676/check")
                    .header("Authorization", "Bearer $token")
                    .contentType(
                        MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                    )
            )
            .andDo(MockMvcResultHandlers.print())
        resultActions1
            .andExpect(MockMvcResultMatchers.handler().methodName("checkCode"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("true"))
        val resultActions2 = mvc
            .perform(
                MockMvcRequestBuilders.get("/seller/business/220818/check")
                    .contentType(
                        MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                    )
            )
            .andDo(MockMvcResultHandlers.print())
        resultActions2
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("false"))
    }

    @Test
    @DisplayName("JWT 로그인 구현")
    @Throws(Exception::class)
    fun t3() {
        register()
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
					
					""".trimIndent(), id, ps
                        )
                    )
                    .contentType(
                        MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                    )
            )
            .andDo(MockMvcResultHandlers.print())

        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk())

        val responseBody = resultActions.andReturn().response.contentAsString

        val parts = responseBody.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        Assertions.assertEquals(2, parts.size, "응답 형식이 올바르지 않음")
        Assertions.assertFalse(parts[0].isEmpty(), "JWT 토큰이 비어 있음")
        Assertions.assertFalse(parts[1].isEmpty(), "API 키가 비어 있음")
    }

    @Test
    @Transactional
    @WithMockUser(username = "test1234", roles = ["SELLER"]) //  SecurityContext 강제설정?
    @DisplayName("판매자 정보 조회")
    @Throws(Exception::class)
    fun t4() {
        val token = login(id, ps)
        val resultActions1 = mvc
            .perform(
                MockMvcRequestBuilders.get("/seller")
                    .header("Authorization", "Bearer $token")
                    .contentType(
                        MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                    )
            )
            .andDo(MockMvcResultHandlers.print())
        resultActions1
            .andExpect(MockMvcResultMatchers.handler().methodName("info"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$.username").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$.companyName").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").isNotEmpty())
    }

    @Test
    @Transactional
    @WithMockUser(username = "test1234", roles = ["SELLER"]) //  SecurityContext 강제설정?
    @DisplayName("판매자 정보 수정")
    @Throws(Exception::class)
    fun t5() {
        val token = login(id, ps)
        val username = "zzzzzzz"
        val password = ps
        val companyName = "sdasdaasdasd"
        val email = "aaaa@naver.com"
        val newPassword = "zzzzzzzzzz"
        val confirmNewPassword = "zzzzzzzzzz"


        val resultActions1 = mvc
            .perform(
                MockMvcRequestBuilders.put("/seller")
                    .header("Authorization", "Bearer $token")
                    .content(
                        String.format(
                            """
					{
						"username": "%s",
					    "password": "%s",
					    "companyName": "%s",
					    "email": "%s",
					    "newPassword": "%s",
					    "confirmNewPassword": "%s"
					    
					}
					
					""".trimIndent(),
                            username,
                            password,
                            companyName,
                            email,
                            newPassword,
                            confirmNewPassword
                        )
                    )
                    .contentType(
                        MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                    )
            )
            .andDo(MockMvcResultHandlers.print())
        resultActions1
            .andExpect(MockMvcResultMatchers.handler().methodName("modify"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("정보수정이 성공했습니다."))
    }
}