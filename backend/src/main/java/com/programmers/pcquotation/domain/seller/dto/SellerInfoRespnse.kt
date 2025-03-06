package com.programmers.pcquotation.domain.seller.dto

import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Data
import lombok.NoArgsConstructor

data class SellerInfoRespnse(
    var id: Long?,
    var username: String?,
    var companyName: String?,
    var email: String?
)
