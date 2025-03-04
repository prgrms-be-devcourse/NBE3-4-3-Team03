package com.programmers.pcquotation.global.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OpenApiStatus {
	run("계속사업자"),
	Suspension("휴업자"),
	Closure("폐업자"),
	None("");
	private final String value;

}
