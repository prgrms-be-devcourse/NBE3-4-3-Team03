package com.programmers.pcquotation.domain.category.dto;

import jakarta.validation.constraints.NotEmpty;

public record CategoryCreateRequest(
	@NotEmpty String category
) {
}
