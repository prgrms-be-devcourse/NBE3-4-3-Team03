package com.programmers.pcquotation.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
public class ErrorResponse {
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String errorCode;

	private String message;

	public ErrorResponse(String message) {
		this.message = message;
	}
}