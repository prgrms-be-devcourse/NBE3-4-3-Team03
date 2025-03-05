package com.programmers.pcquotation.domain.estimaterequest.dto

import com.programmers.pcquotation.domain.estimaterequest.entity.EstimateRequest
import com.programmers.pcquotation.domain.estimaterequest.entity.EstimateRequestStatus
import java.time.LocalDateTime

data class EstimateRequestResDto(
    var id: Int,
    var customerId: String,
    var status: EstimateRequestStatus,
    var createDate: LocalDateTime,
    var budget: Int,
    var purpose: String,
    var otherRequest: String
) {
    constructor(estimateRequest: EstimateRequest) : this(
        id = estimateRequest.id,
        customerId = estimateRequest.customer.toString(),
        status = estimateRequest.status,
        createDate = estimateRequest.createDate,
        budget = estimateRequest.budget,
        purpose = estimateRequest.purpose,
        otherRequest = estimateRequest.otherRequest
    )
}
