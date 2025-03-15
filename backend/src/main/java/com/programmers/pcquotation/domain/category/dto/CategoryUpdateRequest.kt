package com.programmers.pcquotation.domain.category.dto;

import jakarta.validation.constraints.NotEmpty;

data class CategoryUpdateRequest(
	@NotEmpty
	val category: String
)