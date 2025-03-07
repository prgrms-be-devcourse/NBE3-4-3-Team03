package com.programmers.pcquotation.domain.estimaterequest.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.programmers.pcquotation.domain.customer.entity.Customer
import com.programmers.pcquotation.domain.estimaterequest.dto.EstimateRequestData
import com.programmers.pcquotation.domain.estimaterequest.service.EstimateRequestService
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


@SpringBootTest
@Transactional
@AutoConfigureMockMvc
open class 견적요청테스트 {

 @MockitoBean
 private lateinit var estimateRequestService: EstimateRequestService

 @Autowired
 private lateinit var mvc: MockMvc

@Test
@WithMockUser(username = "lee", roles = ["CUSTOMER"])
 fun 견적_요청_생성() {
  val customer =  Customer()
 val estimateRequestData = EstimateRequestData("test purpose", 100, "")
 val requestBody = ObjectMapper().writeValueAsString(estimateRequestData)

 Mockito.doNothing().`when`(estimateRequestService).createEstimateRequest(estimateRequestData, customer)

 val resultActions = mvc.perform(
  MockMvcRequestBuilders.post("/estimate/request")
   .contentType(MediaType.APPLICATION_JSON)
   .content(requestBody)
   .with(csrf())
 )
  .andDo(MockMvcResultHandlers.print())


 resultActions
  .andExpect(MockMvcResultMatchers.status().isCreated)
  .andExpect(MockMvcResultMatchers.content().string("견적 요청이 생성되었습니다"))

 Mockito.verify(estimateRequestService).createEstimateRequest(estimateRequestData, customer)
}

 @Test
 @WithMockUser
 fun 견적_요청_수정_성공() {}

 @Test
 @WithMockUser
 fun 견적_요청_수정_실패() {}

 @Test
 @WithMockUser
 fun 견적_요청_삭제_성공() {}

 @Test
 @WithMockUser
 fun 견적_요청_삭제_실패() {}

 @Test
 @WithMockUser
 fun 견적_요청_단건_조회_성공() {}

 @Test
 @WithMockUser
 fun 견적_요청_단건_조회_실패() {}

 @Test
 @WithMockUser
 fun 견적_요청_전체_조회() {}

}