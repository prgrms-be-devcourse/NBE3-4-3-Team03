package com.programmers.pcquotation.domain.member.dto

import com.programmers.pcquotation.global.enums.UserType
import lombok.ToString

@ToString
data class OAuth2AuthInfoDto(
    val code: String,
    val error: String,
    val state: String,
    val userType: UserType
)