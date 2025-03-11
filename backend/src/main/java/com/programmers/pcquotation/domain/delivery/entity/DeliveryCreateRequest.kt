package com.programmers.pcquotation.domain.delivery.entity

import jakarta.validation.constraints.NotBlank

data class DeliveryCreateRequest(@NotBlank val address: String)

