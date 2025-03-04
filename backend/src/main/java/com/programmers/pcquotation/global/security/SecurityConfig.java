package com.programmers.pcquotation.global.security;

import java.security.Principal;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.programmers.pcquotation.domain.estimate.dto.EstimateSellerResDto;
import com.programmers.pcquotation.domain.estimate.dto.ReceivedQuoteDTO;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	private final CustomAuthenticationFilter customAuthenticationFilter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		HttpSecurity httpSecurity = http.authorizeHttpRequests(authorizeRequests ->
				authorizeRequests
					// 판매자,구매자 정보 관련 권한 설정
					.requestMatchers(HttpMethod.GET,"/seller/business/**")
					.permitAll()
					.requestMatchers("/seller/**")
					.hasRole("SELLER")

					.requestMatchers("/customer/**")
					.hasRole("CUSTOMER")

					// 견적 요청 관련 권한 설정
					.requestMatchers(HttpMethod.POST, "/estimate/request/**")
					.hasRole("CUSTOMER")
					.requestMatchers(HttpMethod.GET, "/estimate/request")
					.hasAnyRole("CUSTOMER", "SELLER")

					// 인증 관련 권한 설정
					.requestMatchers("/api/auth/**")
					.permitAll()

					//아이템 관련 권한 설정
					.requestMatchers(HttpMethod.GET, "/api/admin/items/**")
					.hasAnyRole("SELLER", "ADMIN")
					.requestMatchers("/api/admin/items/**")
					.hasRole("ADMIN")

					// 견적 관련 권한 설정
					.requestMatchers(HttpMethod.GET, "/api/estimate/{id}")
					.hasAnyRole("CUSTOMER", "SELLER")
					.requestMatchers(HttpMethod.POST, "/api/estimate/**")
					.hasRole("SELLER")
					.requestMatchers(HttpMethod.GET, "/api/estimate/seller/**")
					.hasRole("SELLER")

					// 이미지 관련 권한 설정
					.requestMatchers("/api/image/**")
					.permitAll()

					// 카테고리 관련 권한 설정
					.requestMatchers(HttpMethod.GET,"/api/admin/categories/**")
					.hasAnyRole("ADMIN", "SELLER")
					.requestMatchers("/api/admin/categories/**")
					.hasAnyRole("ADMIN")
					// 그 외 모든 요청 허용

					.anyRequest().permitAll()
			)
			.cors(cors -> corsConfigurationSource())
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(sessionManagementConfigurer ->
				sessionManagementConfigurer
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.exceptionHandling(
				exceptionHandling -> exceptionHandling
					.authenticationEntryPoint(
						(request, response, authException) -> {
							response.setContentType("application/json;charset=UTF-8");
							response.setStatus(401);
							response.getWriter().write(
								"사용자 인증정보가 올바르지 않습니다."
							);
						}
					)
					.accessDeniedHandler(
						(request, response, accessDeniedException) -> {
							response.setContentType("application/json;charset=UTF-8");

							response.setStatus(403);
							response.getWriter().write(
								"권한이 없습니다."

							);
						}
					)
			);

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();

		config.setAllowCredentials(true);
		config.setAllowedOrigins(List.of("http://localhost:3000"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setExposedHeaders(List.of("*"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
}