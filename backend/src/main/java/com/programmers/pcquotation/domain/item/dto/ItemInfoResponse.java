package com.programmers.pcquotation.domain.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record ItemInfoResponse(
	@NonNull
	Long id,

	@NotBlank
	String name,

	@NonNull
	Long categoryId,

	@NotBlank
	String categoryName,

	@NotBlank
	String filename
) {
}
