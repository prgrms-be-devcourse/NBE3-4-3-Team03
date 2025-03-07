package com.programmers.pcquotation.domain.estimate.controller

import com.programmers.pcquotation.domain.estimate.dto.*
import com.programmers.pcquotation.domain.estimate.service.EstimateService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/estimate")
class EstimateController(
    private val estimateService: EstimateService
) {
    @PostMapping
    fun createEstimate(
        @RequestBody request: EstimateCreateRequest,
        principal: Principal
    ): ResponseEntity<String> {
        estimateService.createEstimate(request, principal.name)
        return ResponseEntity.ok().body("")
    }

    @PutMapping
    fun updateEstimate(@RequestBody request: EstimateUpdateReqDto): ResponseEntity<String> {
        estimateService.updateEstimate(request)
        return ResponseEntity.ok().body("")
    }

    @DeleteMapping("/{id}")
    fun deleteEstimate(@PathVariable("id") id: Int): ResponseEntity<String> {
        estimateService.deleteEstimate(id)
        return ResponseEntity.ok().body("");
    }

    @GetMapping("/estimate-request/{id}")
    fun getEstimatesByEstimateRequest(
        @PathVariable("id") id: Int
    ): ResponseEntity<List<EstimateResponse>> {
        val estimates = estimateService.getEstimatesByEstimateRequest(id)
        return ResponseEntity(estimates, HttpStatus.OK)
    }

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/seller/{id}")
    fun getEstimatesBySeller(
        @PathVariable("id") id: Int
    ): ResponseEntity<List<EstimateResponse>> {
        val estimates = estimateService.getEstimatesBySeller(id)
        return ResponseEntity(estimates, HttpStatus.OK)
    }
}