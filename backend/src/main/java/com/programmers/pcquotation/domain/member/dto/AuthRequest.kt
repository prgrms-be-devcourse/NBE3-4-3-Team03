package com.programmers.pcquotation.domain.member.dto

import lombok.Builder
import lombok.Getter


data class AuthRequest(
    var userType: String
)