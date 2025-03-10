package com.programmers.pcquotation.domain.estimaterequest.controller

import com.programmers.pcquotation.domain.alarm.AlarmService
import com.programmers.pcquotation.domain.estimaterequest.dto.EstimateRequestData
import com.programmers.pcquotation.domain.estimaterequest.dto.EstimateRequestResDto
import com.programmers.pcquotation.domain.estimaterequest.service.EstimateRequestService
import com.programmers.pcquotation.global.enums.UserType
import com.programmers.pcquotation.global.rq.Rq
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/estimate/request")
class EstimateRequestController(
    private val estimateRequestService: EstimateRequestService,
    private val alarmService: AlarmService,
    private val rq: Rq
) {

    @PostMapping
    fun createEstimateRequest(
        @RequestBody estimateRequestData: @Valid EstimateRequestData,
        principal: Principal
    ): ResponseEntity<String> {
        val customer = estimateRequestService.findCustomer(principal.name)
        estimateRequestService.createEstimateRequest(estimateRequestData, customer)
        alarmService.createEstimateRequestAlarmToAllSeller()
        return ResponseEntity.status(HttpStatus.CREATED).body("견적 요청이 생성되었습니다")
    }

    @PutMapping("/{id}")
    fun modifyEstimateRequest(@PathVariable id: Int, @RequestBody estimateRequestData: @Valid EstimateRequestData
    ): ResponseEntity<String> {
        estimateRequestService.modify(id, estimateRequestData)
        return ResponseEntity.status(HttpStatus.OK).body("수정되었습니다")
    }

    @DeleteMapping("/{id}")
    fun deleteEstimateRequest(@PathVariable id: Int
    ): ResponseEntity<String> {
        estimateRequestService.deleteByEstimateId(id)
        return ResponseEntity.status(HttpStatus.OK).body("삭제되었습니다")
    }

    @GetMapping
    fun getEstimateRequest(principal: Principal): ResponseEntity<List<EstimateRequestResDto?>?> {
        val type = rq.getCookieValue("userType")
        val userType = UserType.fromString(type)
        var list: List<EstimateRequestResDto?>? = null

        when (userType) {
            UserType.CUSTOMER -> {
                val customer = estimateRequestService.findCustomer(principal.name)
                list = estimateRequestService.getEstimateRequestByCustomerId(customer)
            }

            UserType.SELLER -> {
                list = estimateRequestService.getAllEstimateRequest()
            }

            UserType.ADMIN -> emptyList<EstimateRequestResDto>()
            UserType.NOTHING -> emptyList<EstimateRequestResDto>()
        }
        return ResponseEntity(list, HttpStatus.OK)
    }
}