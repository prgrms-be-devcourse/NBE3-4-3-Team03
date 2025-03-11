package com.programmers.pcquotation.global.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OpenApiStatus {
	RUN("계속사업자"),
	SUSPENSION("휴업자"),
	CLOSURE("폐업자"),
	NONE("");
	public final String value;

}
