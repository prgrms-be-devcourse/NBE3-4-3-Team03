package com.programmers.pcquotation.domain.member.exception;

public class TokenNotFoundException extends RuntimeException {
	public TokenNotFoundException(String message) {
		super(message);
	}
}