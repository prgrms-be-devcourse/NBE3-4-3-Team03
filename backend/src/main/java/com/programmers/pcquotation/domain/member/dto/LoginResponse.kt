package com.programmers.pcquotation.domain.member.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.programmers.pcquotation.global.enums.UserType


@JsonInclude(JsonInclude.Include.NON_NULL)
class LoginResponse(
    val apiKey: String,
    val accessToken: String,
    val userType: UserType,
    val message: String,
)