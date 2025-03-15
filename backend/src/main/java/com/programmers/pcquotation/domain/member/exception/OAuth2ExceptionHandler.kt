package com.programmers.pcquotation.domain.member.exception

import org.springframework.http.HttpHeaders.LOCATION
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class OAuth2ExceptionHandler {
    @ExceptionHandler(Oauth2InfoResponseException::class)
    fun handleOAuth2InfoResponse(ex: Oauth2InfoResponseException): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.FOUND)
            .header(LOCATION, "http://localhost:3000/social/error/${ex.message}")
            .build()
    }

    @ExceptionHandler(Oauth2LoginException::class)
    fun handleOAuth2Login(ex: Oauth2LoginException): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.FOUND)
            .header(LOCATION, "http://localhost:3000/social/error/${ex.message}")
            .build()
    }

    @ExceptionHandler(RedirectInfoException::class)
    fun handleRedirectInfo(ex: RedirectInfoException): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.FOUND)
            .header(LOCATION, "http://localhost:3000/social/error/${ex.message}")
            .build()
    }

    @ExceptionHandler(TokenNotFoundException::class)
    fun handleTokenNotFound(ex: TokenNotFoundException): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.FOUND)
            .header(LOCATION, "http://localhost:3000/social/error/${ex.message}")
            .build()
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(ex: UserNotFoundException): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.FOUND)
            .header(LOCATION, "http://localhost:3000/social/error/${ex.message}")
            .build()
    }
}