package com.programmers.pcquotation.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import kotlin.jvm.Throws;

@Configuration
class SecurityConfig(private val customAuthenticationFilter: CustomAuthenticationFilter) {

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests { authorizeRequests ->
            authorizeRequests
                // 판매자, 구매자 정보 관련 권한 설정
                .requestMatchers(HttpMethod.GET, "/seller/business/**").permitAll()
                .requestMatchers("/seller/**").hasRole("SELLER")
                .requestMatchers("/customer/**").hasRole("CUSTOMER")

                // 견적 요청 관련 권한 설정
                .requestMatchers(HttpMethod.POST, "/estimate/request/**").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.GET, "/estimate/request").hasAnyRole("CUSTOMER", "SELLER")

                // 인증 관련 권한 설정
                .requestMatchers("/api/auth/**").permitAll()

                // 아이템 관련 권한 설정
                .requestMatchers(HttpMethod.GET, "/api/admin/items/**").hasAnyRole("SELLER", "ADMIN")
                .requestMatchers("/api/admin/items/**").hasRole("ADMIN")

                // 견적 관련 권한 설정
                .requestMatchers(HttpMethod.GET, "/api/estimate/{id}").hasAnyRole("CUSTOMER", "SELLER")
                .requestMatchers(HttpMethod.POST, "/api/estimate/**").hasRole("SELLER")
                .requestMatchers(HttpMethod.GET, "/api/estimate/seller/**").hasRole("SELLER")

                // 이미지 관련 권한 설정
                .requestMatchers("/api/image/**").permitAll()

                // 카테고리 관련 권한 설정
                .requestMatchers(HttpMethod.GET, "/api/admin/categories/**").hasAnyRole("ADMIN", "SELLER")
                .requestMatchers("/api/admin/categories/**").hasRole("ADMIN")

                // 그 외 모든 요청 허용
                .anyRequest().permitAll()
        }
            .cors { cors -> corsConfigurationSource() }
            .csrf { csrf -> csrf.disable() }
            .sessionManagement { sessionManagementConfigurer ->
                sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling { exceptionHandling ->
                exceptionHandling
                    .authenticationEntryPoint { request, response, authException ->
                        response.contentType = "application/json;charset=UTF-8"
                        response.status = 401
                        response.writer.write("사용자 인증정보가 올바르지 않습니다.")
                    }
                    .accessDeniedHandler { request, response, accessDeniedException ->
                        response.contentType = "application/json;charset=UTF-8"
                        response.status = 403
                        response.writer.write("권한이 없습니다.")
                    }
            }

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration()

        config.allowCredentials = true
        config.allowedOrigins = listOf("http://localhost:3000")
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        config.allowedHeaders = listOf("*")
        config.exposedHeaders = listOf("*")

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }
}