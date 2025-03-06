package com.programmers.pcquotation.domain.customer.exception

import com.programmers.pcquotation.domain.member.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class CustomerExceptionHandler {
    @ExceptionHandler(PasswordMismatchException::class)
    fun handlePasswordMismatch(ex: PasswordMismatchException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse("Password mismatch"))
    }

    @ExceptionHandler(CustomerAlreadyExistException::class)
    fun handleCustomerAlreadyExist(ex: CustomerAlreadyExistException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ErrorResponse("User already exists"))
    }

    @ExceptionHandler(IncorrectLoginAttemptException::class)
    fun handleIncorrectLoginAttempt(ex: IncorrectLoginAttemptException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse("Incorrect ID or Password"))
    }
}