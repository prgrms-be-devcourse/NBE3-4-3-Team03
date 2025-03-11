package com.programmers.pcquotation.domain.estimate.dto

import com.programmers.pcquotation.domain.estimaterequest.entity.EstimateRequest
import java.time.LocalDateTime

data class EstimateRequestResponse(
    val id: Int,
    val customerId: String,
    val purpose: String,
    val budget: Int,
    val status: String,
    val createDate: LocalDateTime
) {
    companion object {
        fun from(estimateRequest: EstimateRequest): EstimateRequestResponse {
            return EstimateRequestResponse(
                id = estimateRequest.id,
                customerId = estimateRequest.customer.customerName,
                purpose = estimateRequest.purpose,
                budget = estimateRequest.budget,
                status = estimateRequest.status.name,
                createDate = estimateRequest.createDate
            )
        }
    }
} 