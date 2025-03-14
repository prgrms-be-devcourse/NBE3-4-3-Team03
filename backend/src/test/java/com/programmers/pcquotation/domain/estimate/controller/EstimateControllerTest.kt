package com.programmers.pcquotation.domain.estimate.controller

import com.programmers.pcquotation.domain.estimate.controller.EstimateController
import com.programmers.pcquotation.domain.seller.service.SellerService
import com.programmers.pcquotation.util.Util
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

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EstimateControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var sellerService: SellerService

    @Test
    @WithMockUser(username = "seller123", roles = ["SELLER"])
    @DisplayName("견적작성 테스트")
    @Throws(
        Exception::class
    )

    fun v1() {
        val token = "Bearer " + Util.loginSeller("seller123", "zzzzz", mvc, sellerService)
        val requestBody = """
			{
			   "estimateRequestId" : 1,
			   "items" : [
			     {
			       "itemId" : 1,
			       "price" : 135000
			     },
			     {
			       "itemId" : 2,
			       "price" : 199000
			     }
			   ]
			 }
			
			""".trimIndent()

        val resultActions = mvc.perform(
            MockMvcRequestBuilders.post("/api/estimate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .characterEncoding(StandardCharsets.UTF_8)
                .header("Authorization", token)
        ).andDo(MockMvcResultHandlers.print())

        // 응답 상태 코드 확인
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk())

        //테스트 추가 검증 필요
    }

    @Test
    @WithMockUser(username = "seller123", roles = ["SELLER"])
    @DisplayName("견적요청 별 견적작성 조회")
    @Throws(
        Exception::class
    )
    fun v2() {
        val token = "Bearer " + Util.loginSeller("seller123", "zzzzz", mvc, sellerService)

        val resultActions = mvc.perform(
            MockMvcRequestBuilders.get("/api/estimate/estimate-request/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .header("Authorization", token)

        ).andDo(MockMvcResultHandlers.print())

        // 응답 상태 코드 확인
        resultActions
            .andExpect(MockMvcResultMatchers.handler().handlerType(EstimateController::class.java))
            .andExpect(MockMvcResultMatchers.handler().methodName("getEstimatesByEstimateRequest"))
            .andExpect(MockMvcResultMatchers.status().isOk()) // 응답 본문이 배열인지 확인
            .andExpect(MockMvcResultMatchers.jsonPath("$").isArray()) // 응답의 각 필드 존재 여부 확인
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].companyName").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].createdDate").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].totalPrice").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].items").exists())
    }
}