package com.programmers.pcquotation.global.rq

import org.springframework.http.ResponseCookie
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.RequestScope
import com.programmers.pcquotation.domain.estimaterequest.exception.NullEntityException
import com.programmers.pcquotation.domain.member.entitiy.Member
import com.programmers.pcquotation.domain.member.service.AuthService
import com.programmers.pcquotation.domain.seller.service.SellerService
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
    private val authService: AuthService,
    private val sellerService: SellerService,
    private val customUserDetailsService: CustomUserDetailsService
) {

    fun setLogin(member: Member) {
        val user = CustomUserDetails(member)
        val authentication: Authentication = UsernamePasswordAuthenticationToken(
            user, user.password, user.authorities
        )
        SecurityContextHolder.getContext().authentication = authentication
    }

    fun getMember(): Member? {
        val authorization = getHeader("Authorization") ?: return null
        if (!authorization.startsWith("Bearer ")) throw NullEntityException()

        val token = authorization.removePrefix("Bearer ")
        val tokenBits = token.split(" ", limit = 3)

        if (tokenBits.size < 3) return null

        return (SecurityContextHolder.getContext().authentication?.principal as? UserDetails)?.let {
            customUserDetailsService.loadUserByUsername(it.username, UserType.valueOf(tokenBits[2]))
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
