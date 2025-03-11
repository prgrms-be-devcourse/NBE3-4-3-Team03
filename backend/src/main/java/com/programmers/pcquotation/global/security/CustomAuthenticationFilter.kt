package com.programmers.pcquotation.global.security;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.programmers.pcquotation.domain.member.entitiy.Member;
import com.programmers.pcquotation.domain.member.service.AuthService;

import com.programmers.pcquotation.global.enums.UserType;
import com.programmers.pcquotation.global.rq.Rq;

import jakarta.servlet.FilterChain;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
class CustomAuthenticationFilter @Autowired
constructor(
    private val authService: AuthService,
    private val rq: Rq
) : OncePerRequestFilter() {

    private data class AuthTokens(
        val apiKey: String?,
        val accessToken: String?,
        val userType: UserType?
    )

    private fun getAuthTokensFromRequest(): AuthTokens? {
        val authorization = rq.getHeader("Authorization")

        authorization?.takeIf { it.startsWith("Bearer ") }?.let {
            val tokenBits = it.removePrefix("Bearer ").split(" ", limit = 3)
            if (tokenBits.size == 3) {
                return AuthTokens(tokenBits[0], tokenBits[1], UserType.fromString(tokenBits[2]))
            }
        }

        val apiKey = rq.getCookieValue("apiKey")
        val accessToken = rq.getCookieValue("accessToken")
        val userType = UserType.fromString(rq.getCookieValue("userType"))

        return if (apiKey != null && accessToken != null && userType != UserType.NOTHING) {
            AuthTokens(apiKey, accessToken, userType)
        } else {
            null
        }
    }

    private fun refreshAccessToken(member: Member, userType: UserType) {
        val newAccessToken = authService.getAccessToken(member)
        rq.setHeader("Authorization", "Bearer ${member.apiKey} $newAccessToken $userType")
        rq.setCookie("accessToken", newAccessToken)
        rq.setCookie("userType", userType.toString())
    }

    private fun refreshAccessTokenByApiKey(apiKey: String, userType: UserType): Member? {
        return authService.findByApiKey(apiKey, userType).orElse(null)?.also {
            refreshAccessToken(it, userType)
        }
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authTokens = getAuthTokensFromRequest()
        if (authTokens == null) {
            filterChain.doFilter(request, response)
            return
        }

        val (apiKey, accessToken, userType) = authTokens

        var member: Member? = authService.getMemberFromAccessToken(accessToken ?: "", userType ?: UserType.NOTHING)
            .orElse(null)

        if (member == null && apiKey != null && userType != null) {
            member = refreshAccessTokenByApiKey(apiKey, userType)
        }

        member?.let { rq.setLogin(it) }

        filterChain.doFilter(request, response)
    }
}