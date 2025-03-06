package com.programmers.pcquotation.domain.member.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.programmers.pcquotation.global.enums.UserType
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor
import org.springframework.lang.NonNull


@JsonInclude(JsonInclude.Include.NON_NULL)
class LoginResponse(
    @NonNull
    var apiKey: String,
    @NonNull
    var accessToken: String,
    @NonNull
    var userType: UserType,
    @NonNull
    var message: String,
)