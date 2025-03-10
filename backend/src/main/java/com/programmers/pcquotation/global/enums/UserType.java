package com.programmers.pcquotation.global.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserType {
	CUSTOMER("CUSTOMER"),
	SELLER("SELLER"),
	ADMIN("ADMIN"),
	NOTHING("NOTHING");
	private final String value;
	public static UserType fromString(String value) {
		if (value == null) {
			return UserType.NOTHING;
		}
		switch (value){
			case "[ROLE_CUSTOMER]": return UserType.CUSTOMER;
			case "[ROLE_SELLER]": return UserType.SELLER;
			case "[ROLE_ADMIN]": return UserType.ADMIN;
		}
		try {
			return UserType.valueOf(value);
		} catch (IllegalArgumentException e) {
			return UserType.NOTHING;
		}
	}

}
