package com.programmers.pcquotation.domain.member.dto

data class OAuth2TokenInfoDto(
    var accessToken: String,
    var refreshToken: String,
    var tokenType: String
)