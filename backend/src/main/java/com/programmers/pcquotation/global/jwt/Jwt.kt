package com.programmers.pcquotation.global.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys

import java.util.*
import javax.crypto.SecretKey

object Jwt {
    fun toString(secret: String, expireSeconds: Long, body: Map<String, Any>): String {
        val issuedAt = Date()
        val expiration = Date(issuedAt.time + 1000L * expireSeconds)

        val secretKey: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

        return Jwts.builder()
            .claims(body)
            .issuedAt(issuedAt)
            .expiration(expiration)
            .signWith(secretKey)
            .compact()
    }

    fun payload(secret: String, jwtStr: String): Map<String, Any>? {
        val secretKey: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())
        return try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseClaimsJws(jwtStr)
                .payload as Map<String, Any>
        } catch (e: Exception) {
            null
        }
    }
}
