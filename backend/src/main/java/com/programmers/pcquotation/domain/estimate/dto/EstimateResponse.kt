package com.programmers.pcquotation.domain.estimate.dto

import java.time.LocalDateTime

data class EstimateResponse(
    val id: Int,
    val purpose: String,
    val budget: Int,
    val customerName: String,
    val companyName: String,
    val createdDate: LocalDateTime,
    val totalPrice: Int,
    val items: Map<String, String>
)