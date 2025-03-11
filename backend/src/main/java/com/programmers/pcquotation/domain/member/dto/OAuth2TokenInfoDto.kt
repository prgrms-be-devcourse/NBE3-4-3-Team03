package com.programmers.pcquotation.domain.member.dto

import lombok.ToString

@ToString
class OAuth2TokenInfoDto {
    var accessToken: String
    var refreshToken: String
    var tokenType: String
    var expiresIn: String? = null
    var error: String? = null
    var errorDescription: String? = null

    constructor(accessToken: String, refreshToken: String, tokenType: String) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        this.tokenType = tokenType
    }

    constructor(accessToken: String, refreshToken: String, tokenType: String, expireIn: String) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        this.tokenType = tokenType
        this.expiresIn = expireIn
    }
}