package com.programmers.pcquotation.domain.delivery.entity

import jakarta.validation.constraints.NotBlank

@JvmRecord
data class DeliveryCreateRequest(@JvmField val address: String)

