package com.programmers.pcquotation.domain.category.dto;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record CategoryInfoResponse(

	@NonNull
	Long id,

	@NonNull
	String category

) {
}
