package com.programmers.pcquotation.domain.member.exception;

public class Oauth2LoginException extends RuntimeException {
	public Oauth2LoginException(String message) {
		super(message);
	}
}