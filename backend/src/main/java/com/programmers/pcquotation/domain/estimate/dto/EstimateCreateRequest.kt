package com.programmers.pcquotation.domain.estimate.dto

data class EstimateCreateRequest(
    val estimateRequestId: Int,
    val items: List<EstimateItemDto>
)