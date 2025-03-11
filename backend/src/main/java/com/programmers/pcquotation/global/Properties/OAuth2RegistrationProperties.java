package com.programmers.pcquotation.global.Properties;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration")
public record OAuth2RegistrationProperties(
	RegistrationConfig kakao,
	RegistrationConfig naver
) {
	public record RegistrationConfig(
		String clientId,
		String clientSecret,
		String redirectUri,
		String authorizationGrantType,
		String clientAuthenticationMethod,
		String clientName,
		List<String> scope
	) {
	}
}