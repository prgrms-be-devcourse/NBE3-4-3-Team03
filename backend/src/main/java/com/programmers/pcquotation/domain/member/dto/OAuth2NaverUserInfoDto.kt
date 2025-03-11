package com.programmers.pcquotation.domain.member.dto

import com.programmers.pcquotation.domain.customer.dto.CustomerSignupRequest
import lombok.ToString


@ToString
data class OAuth2NaverUserInfoDto(
    val resultCode: String,
    val message: String,
    val response: NaverUserResponse,
) {
    val provider = "KAKAO"

    @ToString
    data class NaverUserResponse(
        val id: String,
        val nickname: String,
        val email: String,
        val name: String
    )

    fun toCustomerSignupRequest(): CustomerSignupRequest {
        return CustomerSignupRequest(
            this.response.email,
            this.response.id,
            this.response.id,
            this.response.nickname,
            this.response.email,
            "",
            ""
        )
    }

    fun toLoginRequest(): LoginRequest {
        return LoginRequest(this.response.email, this.response.id)
    }

}