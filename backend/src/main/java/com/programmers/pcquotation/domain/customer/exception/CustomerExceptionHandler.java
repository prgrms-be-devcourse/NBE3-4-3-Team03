package com.programmers.pcquotation.domain.customer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.programmers.pcquotation.domain.member.dto.ErrorResponse;

@ControllerAdvice
public class CustomerExceptionHandler {
	@ExceptionHandler(PasswordMismatchException.class)
	public ResponseEntity<ErrorResponse> handlePasswordMismatch(PasswordMismatchException ex) {
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(new ErrorResponse("Password mismatch"));
	}

	@ExceptionHandler(CustomerAlreadyExistException.class)
	public ResponseEntity<ErrorResponse> handleCustomerAlreadyExist(CustomerAlreadyExistException ex) {
		return ResponseEntity
			.status(HttpStatus.CONFLICT)
			.body(new ErrorResponse("User already exists"));
	}

	@ExceptionHandler(IncorrectLoginAttemptException.class)
	public ResponseEntity<ErrorResponse> handleIncorrectLoginAttempt(IncorrectLoginAttemptException ex) {
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorResponse("Incorrect ID or Password"));
	}
}