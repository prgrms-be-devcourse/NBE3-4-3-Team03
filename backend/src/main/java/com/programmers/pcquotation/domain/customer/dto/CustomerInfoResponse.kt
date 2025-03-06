package com.programmers.pcquotation.domain.customer.dto

import lombok.Builder
import lombok.Getter


data class CustomerInfoResponse (
    var id: Long?,
    var username: String?,
    var customerName: String?,
    var email: String?
)