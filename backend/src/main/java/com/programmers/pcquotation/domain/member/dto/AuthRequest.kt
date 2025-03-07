package com.programmers.pcquotation.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuthRequest {
	String userType;
}
