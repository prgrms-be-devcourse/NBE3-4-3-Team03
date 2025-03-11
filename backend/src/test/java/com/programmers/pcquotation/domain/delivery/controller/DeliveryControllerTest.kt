package com.programmers.pcquotation.domain.delivery.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.programmers.pcquotation.domain.delivery.entity.DeliveryCreateRequest
import com.programmers.pcquotation.domain.delivery.entity.DeliveryDto
import com.programmers.pcquotation.domain.delivery.entity.DeliveryStatus
import com.programmers.pcquotation.domain.delivery.exception.NullEntityException
import com.programmers.pcquotation.domain.delivery.service.DeliveryService
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
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
class 배송_테스트 {

 @MockitoBean
 private lateinit var deliveryService: DeliveryService

 @Autowired
 private lateinit var mvc:MockMvc

 @Test
 @WithMockUser
 fun 배송_생성() {
  val request = DeliveryCreateRequest("test address")
  val requestBody = ObjectMapper().writeValueAsString(request)
  val id = 1

  Mockito.doNothing().`when`(deliveryService).create(request, id)

  val resultActions = mvc.perform(
   MockMvcRequestBuilders.post("/delivery")
    .param("id", id.toString())
    .contentType(MediaType.APPLICATION_JSON)
    .content(requestBody)
  )
   .andDo(MockMvcResultHandlers.print())

  resultActions
   .andExpect(MockMvcResultMatchers.status().isCreated)
   .andExpect(MockMvcResultMatchers.content().string("주문이 완료되었습니다."))

  Mockito.verify(deliveryService).create(request, id)
 }


 @Test
 @WithMockUser
 fun 배송_단건_조회_성공() {

  val mockDelivery = DeliveryDto(1, "test address", 1, DeliveryStatus.ORDER_COMPLETED)
  Mockito.`when`(deliveryService.findByDeliveryId(ArgumentMatchers.anyInt())).thenReturn(mockDelivery)

  val resultActions = mvc.perform(
   MockMvcRequestBuilders.get("/delivery/1")
  ).andDo(MockMvcResultHandlers.print())

  resultActions
   .andExpect(MockMvcResultMatchers.status().isOk)
   .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
   .andExpect(MockMvcResultMatchers.jsonPath("$.address").value("test address"))
   .andExpect(MockMvcResultMatchers.jsonPath("$.estimateId").value(1))
   .andExpect(MockMvcResultMatchers.jsonPath("$.status").isString)
 }

 @Test
 @WithMockUser
 fun 배송_단건_조회_실패() {

  Mockito.`when`(deliveryService.findByDeliveryId(10)).thenThrow(NullEntityException())

  val resultActions = mvc.perform(
   MockMvcRequestBuilders.get("/delivery/10")
  ).andDo(MockMvcResultHandlers.print())

  resultActions
   .andExpect(MockMvcResultMatchers.status().isBadRequest)
   .andExpect(MockMvcResultMatchers.content().string("다시 실행해주세요"))
 }

 @Test
 @WithMockUser
 fun 배송_전체_조회_성공() {
  val deliveryList = mutableListOf<DeliveryDto>()
  for (i in 1..10){
   deliveryList.add(
    DeliveryDto(i, "test address$i", 1, DeliveryStatus.ORDER_COMPLETED))
  }

  Mockito.`when`(deliveryService.findAll()).thenReturn(deliveryList)

  val resultActions = mvc.perform(
   MockMvcRequestBuilders.get("/delivery")
  ).andDo(MockMvcResultHandlers.print())

  resultActions
   .andExpect(MockMvcResultMatchers.status().isOk)
   .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(10))

  for (i in 0..9){
   resultActions
    .andExpect(MockMvcResultMatchers.jsonPath("$[$i].id").value(i+1))
  }
 }

 @Test
 @WithMockUser
 fun 배송_전체_조회_빈리스트() {
  val deliveryList = mutableListOf<DeliveryDto>()
  Mockito.`when`(deliveryService.findAll()).thenReturn(deliveryList)

  val resultActions = mvc.perform(
   MockMvcRequestBuilders.get("/delivery")
  ).andDo(MockMvcResultHandlers.print())

  resultActions
   .andExpect(MockMvcResultMatchers.status().isOk)
   .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0))
 }



@Test
@WithMockUser
 fun 배송_삭제_성공() {
  val id = 1
 Mockito.doNothing().`when`(deliveryService).deleteByDeliveryId(id)

 val resultActions = mvc.perform(
  MockMvcRequestBuilders.delete("/delivery/1")
 ).andDo(MockMvcResultHandlers.print())

 resultActions
  .andExpect(MockMvcResultMatchers.status().isOk)
  .andExpect(MockMvcResultMatchers.content().string("주문이 취소되었습니다."))

 Mockito.verify(deliveryService).deleteByDeliveryId(id)
 }

 @Test
 @WithMockUser
 fun 배송_삭제_실패() {
  Mockito.`when`(deliveryService.deleteByDeliveryId(1)).thenThrow(NullEntityException())

  val resultActions = mvc.perform(
   MockMvcRequestBuilders.delete("/delivery/1")
  ).andDo(MockMvcResultHandlers.print())

  resultActions
   .andExpect(MockMvcResultMatchers.status().isBadRequest)
   .andExpect(MockMvcResultMatchers.content().string("다시 실행해주세요"))
 }

 @Test
 @WithMockUser
 fun 배송_수정_성공() {
  val id = 1
  val request = DeliveryCreateRequest("modify address")
  val requestBody = ObjectMapper().writeValueAsString(request)
  Mockito.doNothing().`when`(deliveryService).modify(id, request)

  val resultActions = mvc.perform(
   MockMvcRequestBuilders.put("/delivery/1")
    .contentType(MediaType.APPLICATION_JSON)
    .content(requestBody)
  )
   .andDo(MockMvcResultHandlers.print())

  resultActions
   .andExpect(MockMvcResultMatchers.status().isOk)
   .andExpect(MockMvcResultMatchers.content().string("주문이 수정되었습니다."))

  Mockito.verify(deliveryService).modify(id, request)
 }

 @Test
 @WithMockUser
 fun 배송_수정_실패() {
  val request = DeliveryCreateRequest("modify address")
  val requestBody = ObjectMapper().writeValueAsString(request)
  Mockito.`when`(deliveryService.modify(1, DeliveryCreateRequest("modify address"))).thenThrow(NullEntityException())

  val resultActions = mvc.perform(
   MockMvcRequestBuilders.put("/delivery/1")
    .contentType(MediaType.APPLICATION_JSON)
    .content(requestBody)
  )
   .andDo(MockMvcResultHandlers.print())

  resultActions
   .andExpect(MockMvcResultMatchers.status().isBadRequest)
   .andExpect(MockMvcResultMatchers.content().string("다시 실행해주세요"))
 }
}