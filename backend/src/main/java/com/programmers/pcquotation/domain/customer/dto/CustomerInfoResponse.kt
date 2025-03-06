package com.programmers.pcquotation.domain.customer.dto

import lombok.Builder
import lombok.Getter


data class CustomerInfoResponse (
    var id: Long? = null,
    var username: String? = null,
    var customerName: String? = null,
    var email: String? = null
)