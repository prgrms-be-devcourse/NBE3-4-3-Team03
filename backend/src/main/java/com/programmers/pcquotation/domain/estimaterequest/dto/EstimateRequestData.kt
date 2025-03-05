package com.programmers.pcquotation.domain.estimaterequest.dto

import jakarta.validation.constraints.NotBlank

@JvmRecord
data class EstimateRequestData(val purpose: @NotBlank String, val budget: Int, val otherRequest: String)
