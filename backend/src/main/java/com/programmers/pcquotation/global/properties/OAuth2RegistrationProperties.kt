package com.programmers.pcquotation.global.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration")
data class OAuth2RegistrationProperties(
    val kakao: RegistrationConfig,
    val naver: RegistrationConfig
) {
    data class RegistrationConfig(
        val clientId: String,
        val clientSecret: String,
        val redirectUri: String,
        val authorizationGrantType: String,
        val clientAuthenticationMethod: String,
        val clientName: String,
        val scope: List<String>? = null
    )
}