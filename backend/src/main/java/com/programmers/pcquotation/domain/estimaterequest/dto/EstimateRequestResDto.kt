package com.programmers.pcquotation.domain.estimaterequest.dto

import com.programmers.pcquotation.domain.estimaterequest.entity.EstimateRequest
import com.programmers.pcquotation.domain.estimaterequest.entity.EstimateRequestStatus
import java.time.LocalDateTime

data class EstimateRequestResDto(
    val id: Int,
    val customerName: String,
    val status: EstimateRequestStatus,
    val createDate: LocalDateTime,
    val budget: Int,
    val purpose: String,
    val otherRequest: String
) {
    constructor(estimateRequest: EstimateRequest) : this(
        id = estimateRequest.id,
        customerName = estimateRequest.customer.customerName,
        status = estimateRequest.status ?: EstimateRequestStatus.WAIT,
        createDate = estimateRequest.createDate,
        budget = estimateRequest.budget,
        purpose = estimateRequest.purpose,
        otherRequest = estimateRequest.otherRequest
    )
}