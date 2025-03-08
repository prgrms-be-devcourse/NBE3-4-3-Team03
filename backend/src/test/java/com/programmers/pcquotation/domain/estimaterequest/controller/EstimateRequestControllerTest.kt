package com.programmers.pcquotation.domain.estimaterequest.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.programmers.pcquotation.domain.customer.entity.Customer
import com.programmers.pcquotation.domain.estimaterequest.dto.EstimateRequestData
import com.programmers.pcquotation.domain.estimaterequest.dto.EstimateRequestResDto
import com.programmers.pcquotation.domain.estimaterequest.entity.EstimateRequest
import com.programmers.pcquotation.domain.estimaterequest.exception.NullEntityException
import com.programmers.pcquotation.domain.estimaterequest.service.EstimateRequestService
import jakarta.servlet.http.Cookie
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class 견적요청테스트 {

 @MockitoBean
 private lateinit var estimateRequestService: EstimateRequestService

 @Autowired
 private lateinit var mvc: MockMvc

 @Test
 @WithMockUser(username = "customer1", roles = ["CUSTOMER"])
 fun 견적_요청_생성() {

  val customer = estimateRequestService.findCustomer("customer1")
  val estimateRequestData = EstimateRequestData("test purpose", 100, "")
  val requestBody = ObjectMapper().writeValueAsString(estimateRequestData)

  Mockito.`when`(estimateRequestService.findCustomer("customer1")).thenReturn(customer)

  Mockito.doNothing().`when`(estimateRequestService).createEstimateRequest(estimateRequestData, customer)

  val resultActions = mvc.perform(
   MockMvcRequestBuilders.post("/estimate/request")
    .contentType(MediaType.APPLICATION_JSON)
    .content(requestBody)
  )
   .andDo(MockMvcResultHandlers.print())

  resultActions
   .andExpect(MockMvcResultMatchers.status().isCreated)
   .andExpect(MockMvcResultMatchers.content().string("견적 요청이 생성되었습니다"))

  Mockito.verify(estimateRequestService).createEstimateRequest(estimateRequestData, customer)
 }

 @Test
 @WithMockUser(username = "customer1", roles = ["CUSTOMER"])
 fun 견적_요청_수정_성공() {
  val id = 1

  val modifyEstimateRequestData = EstimateRequestData("modify purpose", 100, "")
  val requestBody = ObjectMapper().writeValueAsString(modifyEstimateRequestData)

  Mockito.doNothing().`when`(estimateRequestService).modify(id, modifyEstimateRequestData)

  val resultActions = mvc.perform(
   MockMvcRequestBuilders.put("/estimate/request/$id")
    .contentType(MediaType.APPLICATION_JSON)
    .content(requestBody)
  )
   .andDo(MockMvcResultHandlers.print())

  resultActions
   .andExpect(MockMvcResultMatchers.status().isOk)
   .andExpect(MockMvcResultMatchers.content().string("수정되었습니다"))

  Mockito.verify(estimateRequestService).modify(id, modifyEstimateRequestData)
 }


 @Test
 @WithMockUser(username = "customer1", roles = ["CUSTOMER"])
 fun 견적_요청_수정_실패() {
  val id = 2;
  val estimateRequestData = EstimateRequestData("test purpose", 100, "")
  val requestBody = ObjectMapper().writeValueAsString(estimateRequestData)

  Mockito.`when`(estimateRequestService.modify(id, estimateRequestData)).thenThrow(NullEntityException())

  val resultActions = mvc.perform(
   MockMvcRequestBuilders.put("/estimate/request/$id")
    .contentType(MediaType.APPLICATION_JSON)
    .content(requestBody)
  )
   .andDo(MockMvcResultHandlers.print())

  resultActions
   .andExpect(MockMvcResultMatchers.status().isBadRequest)
   .andExpect(MockMvcResultMatchers.content().string("입력한 내용을 다시 확인해주세요"))
 }

 @Test
 @WithMockUser(username = "customer1", roles = ["CUSTOMER"])
 fun 견적_요청_삭제_성공() {
  val id = 1

  Mockito.doNothing().`when`(estimateRequestService).deleteByEstimateId(id)

  val resultActions = mvc.perform(
   MockMvcRequestBuilders.delete("/estimate/request/1")
  )
   .andDo(MockMvcResultHandlers.print())

  resultActions
   .andExpect(MockMvcResultMatchers.status().isOk)
   .andExpect(MockMvcResultMatchers.content().string("삭제되었습니다"))

  Mockito.verify(estimateRequestService).deleteByEstimateId(id)
 }

 @Test
 @WithMockUser(username = "customer1", roles = ["CUSTOMER"])
 fun 견적_요청_삭제_실패() {
  val id = 2

  Mockito.`when`(estimateRequestService.deleteByEstimateId(id)).thenThrow(NullEntityException())

  val resultActions = mvc.perform(
   MockMvcRequestBuilders.delete("/estimate/request/$id")
  )
   .andDo(MockMvcResultHandlers.print())

  resultActions
   .andExpect(MockMvcResultMatchers.status().isBadRequest)
   .andExpect(MockMvcResultMatchers.content().string("입력한 내용을 다시 확인해주세요"))
 }

 @Test
 @WithMockUser(username = "customer1", roles = ["CUSTOMER"])
 fun 구매자_견적_요청_조회_성공() {
  val customer = Customer("customer1", "1234")
  Mockito.`when`(estimateRequestService.findCustomer("customer1")).thenReturn(customer)
  val customerEstimateRequestList = mutableListOf<EstimateRequestResDto>()

  for (i in 1..10) {
   val estimateRequestData = EstimateRequestData("test purpose$i", 100, "")
   val estimateRequest = EstimateRequest(estimateRequestData, customer)
   estimateRequest.id = i
   val estimateRequestResDto = EstimateRequestResDto(estimateRequest)
   customerEstimateRequestList.add(estimateRequestResDto)
  }

  Mockito.`when`(estimateRequestService.getEstimateRequestByCustomerId(customer))
   .thenReturn(customerEstimateRequestList)

  val resultActions = mvc.perform(
   MockMvcRequestBuilders.get("/estimate/request")
    .cookie(Cookie("userType", "CUSTOMER"))
  ).andDo(MockMvcResultHandlers.print())

  resultActions
   .andExpect(MockMvcResultMatchers.status().isOk)
   .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(10))

  for (i in 0..9) {
   resultActions
    .andExpect(MockMvcResultMatchers.jsonPath("$[$i].id").value(i + 1))
  }
 }

 @Test
 @WithMockUser(username = "seller1", roles = ["SELLER"])
 fun 판매자_견적_요청_조회_성공() {
  val customer1 = Customer("customer1", "1234")
  Mockito.`when`(estimateRequestService.findCustomer("customer1")).thenReturn(customer1)

  val customer2 = Customer("customer2", "1234")
  Mockito.`when`(estimateRequestService.findCustomer("customer2")).thenReturn(customer2)

  val customerEstimateRequestList = mutableListOf<EstimateRequestResDto>()

  for (i in 1..10) {
   val estimateRequestData1 = EstimateRequestData("test purpose user1 $i", 100, "")
   val estimateRequest1 = EstimateRequest(estimateRequestData1, customer1)
   estimateRequest1.id = i
   val estimateRequestResDto1 = EstimateRequestResDto(estimateRequest1)
   customerEstimateRequestList.add(estimateRequestResDto1)

   val estimateRequestData2 = EstimateRequestData("test purpose user2 $i", 100, "")
   val estimateRequest2 = EstimateRequest(estimateRequestData2, customer2)
   estimateRequest2.id = i
   val estimateRequestResDto2 = EstimateRequestResDto(estimateRequest2)
   customerEstimateRequestList.add(estimateRequestResDto2)
  }

  Mockito.`when`(estimateRequestService.getAllEstimateRequest())
   .thenReturn(customerEstimateRequestList)

  val resultActions = mvc.perform(
   MockMvcRequestBuilders.get("/estimate/request")
    .cookie(Cookie("userType", "SELLER"))
  ).andDo(MockMvcResultHandlers.print())

  resultActions
   .andExpect(MockMvcResultMatchers.status().isOk)
   .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(20))

  for (i in 0..19) {
   if (i%2==0){
    resultActions
     .andExpect(MockMvcResultMatchers.jsonPath("$[$i].id").value(i/2 + 1))
   }else{
    resultActions
     .andExpect(MockMvcResultMatchers.jsonPath("$[$i].id").value(i/2 + 1))
   }
  }
 }
}
