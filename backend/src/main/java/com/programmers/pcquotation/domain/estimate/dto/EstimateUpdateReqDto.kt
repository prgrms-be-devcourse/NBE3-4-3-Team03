package com.programmers.pcquotation.domain.estimate.dto

data class EstimateUpdateReqDto(
    val estimateId: Int,
    val items: List<EstimateItemDto>
)