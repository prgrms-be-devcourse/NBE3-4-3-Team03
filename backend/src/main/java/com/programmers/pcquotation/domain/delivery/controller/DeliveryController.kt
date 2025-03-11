package com.programmers.pcquotation.domain.delivery.controller

import com.programmers.pcquotation.domain.alarm.service.AlarmService
import com.programmers.pcquotation.domain.delivery.entity.DeliveryCreateRequest
import com.programmers.pcquotation.domain.delivery.entity.DeliveryDto
import com.programmers.pcquotation.domain.delivery.service.DeliveryService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/delivery")
class DeliveryController(
    private val deliveryService: DeliveryService,
    private val alarmService: AlarmService
) {

    @GetMapping
    fun getDeliveryList()
    : ResponseEntity<List<DeliveryDto>>
    {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(deliveryService.findAll())
    }

    @GetMapping("/{id}")
    fun getDeliveryDetail(@PathVariable id: Int)
    : ResponseEntity<DeliveryDto>
    {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(deliveryService.findByDeliveryId(id))
    }

    @PostMapping
    fun createDelivery(
        @RequestBody deliveryCreateRequest: @Valid DeliveryCreateRequest, @RequestParam("id") estimateId: Int)
    : ResponseEntity<String>
    {
        deliveryService.create(deliveryCreateRequest, estimateId)
        alarmService.adoptAlarmToSeller(estimateId)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body("주문이 완료되었습니다.")
    }

    @DeleteMapping("/{id}")
    fun deleteDelivery(@PathVariable id: Int)
    : ResponseEntity<String>
    {
        deliveryService.deleteByDeliveryId(id)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body("주문이 취소되었습니다.")
    }

    @PutMapping("/{id}")
    fun modifyDelivery(@PathVariable id: Int, @RequestBody deliveryCreateRequest: @Valid DeliveryCreateRequest)
    : ResponseEntity<String>
    {
        deliveryService.modify(id, deliveryCreateRequest)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body("주문이 수정되었습니다.")
    }
}
