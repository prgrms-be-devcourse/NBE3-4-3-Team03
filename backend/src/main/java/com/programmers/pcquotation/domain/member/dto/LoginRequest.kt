package com.programmers.pcquotation.domain.member.dto

import lombok.AllArgsConstructor
import lombok.Getter


data class LoginRequest (
    var username: String,
    var password: String
)