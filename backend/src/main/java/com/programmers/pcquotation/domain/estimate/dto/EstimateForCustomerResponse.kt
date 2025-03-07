package com.programmers.pcquotation.domain.estimate.dto

import java.time.LocalDateTime

data class EstimateForCustomerResponse(
    val id: Int,
    val companyName: String,
    val createdDate: LocalDateTime,
    val totalPrice: Int,
    val items: Map<String, String>
)