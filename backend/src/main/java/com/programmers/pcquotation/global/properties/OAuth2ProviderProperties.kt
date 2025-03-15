package com.programmers.pcquotation.global.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.security.oauth2.client.provider")
data class OAuth2ProviderProperties(
    val kakao: ProviderConfig,
    val naver: ProviderConfig
) {
    data class ProviderConfig(
        val authorizationUri: String,
        val tokenUri: String,
        val userInfoUri: String,
        val userNameAttribute: String
    )
}