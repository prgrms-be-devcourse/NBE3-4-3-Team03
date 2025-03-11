package com.programmers.pcquotation.domain.member.exception

import org.springframework.http.HttpHeaders.LOCATION
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class Oauth2ExceptionHandler {

    @ExceptionHandler(Oauth2InfoResponseException::class)
    fun handleOauth2InfoResponse(ex: Oauth2InfoResponseException): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.FOUND)
            .header(LOCATION, "http://localhost:3000/social/error/${ex.message}")
            .build<Any>()
    }

    @ExceptionHandler(Oauth2LoginException::class)
    fun handleOauth2Login(ex: Oauth2LoginException): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.FOUND)
            .header(LOCATION, "http://localhost:3000/social/error/${ex.message}")
            .build<Any>()
    }

    @ExceptionHandler(RedirectInfoException::class)
    fun handleRedirectInfo(ex: RedirectInfoException): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.FOUND)
            .header(LOCATION, "http://localhost:3000/social/error/${ex.message}")
            .build<Any>()
    }

    @ExceptionHandler(TokenNotFoundException::class)
    fun handleTokenNotFound(ex: TokenNotFoundException): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.FOUND)
            .header(LOCATION, "http://localhost:3000/social/error/${ex.message}")
            .build<Any>()
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(ex: UserNotFoundException): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.FOUND)
            .header(LOCATION, "http://localhost:3000/social/error/${ex.message}")
            .build<Any>()
    }


}