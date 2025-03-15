package com.programmers.pcquotation.global.rq

import org.springframework.http.ResponseCookie
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.RequestScope
import com.programmers.pcquotation.domain.estimaterequest.exception.NullEntityException
import com.programmers.pcquotation.domain.member.entity.Member
import com.programmers.pcquotation.global.enums.UserType
import com.programmers.pcquotation.global.security.CustomUserDetails
import com.programmers.pcquotation.global.security.CustomUserDetailsService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

import org.springframework.beans.factory.annotation.Autowired

@RequestScope
@Component
class Rq @Autowired constructor(
    private val req: HttpServletRequest,
    private val resp: HttpServletResponse,
    private val customUserDetailsService: CustomUserDetailsService
) {

    fun setLogin(member: Member) {
        val user = CustomUserDetails(member)
        val authentication: Authentication = UsernamePasswordAuthenticationToken(
            user, user.password, user.authorities
        )
        SecurityContextHolder.getContext().authentication = authentication
    }

    fun getMember(): Member {
        try {
            val authorization = getHeader("Authorization")
                .takeIf { !it.isBlank() } ?: throw NullEntityException("인증 정보가 없습니다.")

            if (!authorization.startsWith("Bearer ")) {
                throw NullEntityException("잘못된 인증 형식입니다.")
            }

            val tokenBits = authorization.removePrefix("Bearer ").split(" ", limit = 3)
            if (tokenBits.size < 3) {
                throw NullEntityException("토큰 형식이 올바르지 않습니다.")
            }

            val userType = UserType.fromString(tokenBits[2])
            if (userType == UserType.NOTHING) {
                throw NullEntityException("유효하지 않은 사용자 타입입니다.")
            }

            val authentication = SecurityContextHolder.getContext().authentication
                ?: throw NullEntityException("인증 컨텍스트가 없습니다.")

            val userDetails = authentication.principal as? UserDetails
                ?: throw NullEntityException("사용자 정보가 올바르지 않습니다.")

            return customUserDetailsService.loadUserByUsername(userDetails.username, userType)
        } catch (e: Exception) {
            when (e) {
                is NullEntityException -> throw e
                else -> throw NullEntityException("사용자 정보 조회 중 오류가 발생했습니다: ${e.message}")
            }
        }
    }

    fun setHeader(name: String, value: String) {
        resp.setHeader(name, value)
    }

    fun getHeader(name: String): String {
        return req.getHeader(name) ?: ""
    }

    fun setCookie(name: String, value: String) {
        val cookie = ResponseCookie.from(name, value)
            .path("/")
            .domain("localhost")
            .sameSite("Strict")
            .secure(true)
            .httpOnly(true)
            .build()
        resp.addHeader("Set-Cookie", cookie.toString())
    }

    fun getCookieValue(name: String): String? {
        return req.cookies?.firstOrNull { it.name == name }?.value
    }

    fun deleteCookie(name: String) {
        val cookie = ResponseCookie.from(name, null)
            .path("/")
            .domain("localhost")
            .sameSite("Strict")
            .secure(true)
            .httpOnly(true)
            .maxAge(0)
            .build()
        resp.addHeader("Set-Cookie", cookie.toString())
    }
}