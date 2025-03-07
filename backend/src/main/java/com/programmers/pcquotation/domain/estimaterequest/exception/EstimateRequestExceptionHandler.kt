package com.programmers.pcquotation.domain.estimaterequest.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.sql.SQLException
import java.util.stream.Collectors

@ControllerAdvice
class EstimateRequestExceptionHandler {
    @ExceptionHandler(NullEntityException::class)
    fun handleException(e: NullEntityException?): ResponseEntity<String> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body("입력한 내용을 다시 확인해주세요")
    }
    @ExceptionHandler(SQLException::class)
    fun handleException(e: SQLException?): ResponseEntity<String> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body("실행 할 수 없습니다.")
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handle(ex: MethodArgumentNotValidException): ResponseEntity<String> {
        val message = ex.bindingResult
            .allErrors
            .stream()
            .filter { error: ObjectError? -> error is FieldError }
            .map { error: ObjectError -> error as FieldError }
            .map { error: FieldError -> error.field + "-" + error.defaultMessage }
            .sorted(Comparator.comparing { obj: String -> obj.toString() })
            .collect(Collectors.joining("\n"))
        return ResponseEntity(message, HttpStatus.BAD_REQUEST)
    }
}