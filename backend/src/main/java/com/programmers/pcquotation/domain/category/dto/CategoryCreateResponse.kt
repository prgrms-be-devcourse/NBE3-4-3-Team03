package com.programmers.pcquotation.domain.category.dto;

import org.springframework.lang.NonNull;

public record CategoryCreateResponse(
	@NonNull
	Long id,

	@NonNull
	String message // 예: "부품이 성공적으로 생성되었습니다."
) {
}

