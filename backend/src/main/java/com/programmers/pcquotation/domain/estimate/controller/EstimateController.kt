package com.programmers.pcquotation.domain.estimate.controller

import com.programmers.pcquotation.domain.alarm.service.AlarmService
import com.programmers.pcquotation.domain.chat.service.ChatRoomService
import com.programmers.pcquotation.domain.chat.service.ChatService
import com.programmers.pcquotation.domain.estimate.dto.*
import com.programmers.pcquotation.domain.estimate.service.EstimateService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/estimate")
class EstimateController(
    private val estimateService: EstimateService,
    private val chatService: ChatService,
    private val chatRoomService: ChatRoomService,
    private val alarmService: AlarmService
) {
    @PostMapping
    fun createEstimate(
        @RequestBody request: EstimateCreateRequest,
        principal: Principal
    ): ResponseEntity<EstimateResponse> {
        val estimate = estimateService.createEstimate(request, principal.name)
        chatRoomService.createChatRoom(estimate)
        alarmService.createEstimateAlarmToCustomer(principal.name)

        return ResponseEntity.ok().body(EstimateResponse("견적 생성 성공"))
    }

    @PutMapping
    fun updateEstimate(@RequestBody request: EstimateUpdateReqDto): ResponseEntity<EstimateResponse> {
        estimateService.updateEstimate(request)

        return ResponseEntity.ok().body(EstimateResponse("견적 수정 성공"))
    }

    @Transactional
    @DeleteMapping("/{id}")
    fun deleteEstimate(@PathVariable("id") id: Int): ResponseEntity<EstimateResponse> {
        chatService.deleteChat(id)
        chatRoomService.deleteChatRoom(id)
        estimateService.deleteEstimate(id)

        return ResponseEntity.ok().body(EstimateResponse("견적 삭제 성공"));
    }

    @GetMapping("/estimate-request/{id}")
    fun getEstimatesByEstimateRequest(
        @PathVariable("id") id: Int,
        @RequestParam(required = false, defaultValue = "LATEST") sortType: EstimateSortType
    ): ResponseEntity<List<EstimateDto>> {
        val estimates = estimateService.getEstimatesByEstimateRequest(id, sortType)

        return ResponseEntity(estimates, HttpStatus.OK)
    }

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/seller/{id}")
    fun getEstimatesBySeller(
        @PathVariable("id") id: Int,
        @PageableDefault(size = 5) pageable: Pageable
    ): ResponseEntity<Page<EstimateDto>> {
        val estimates = estimateService.getEstimatesBySeller(id, pageable)

        return ResponseEntity(estimates, HttpStatus.OK)
    }
}