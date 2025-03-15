package com.programmers.pcquotation.domain.category.dto;

import jakarta.validation.constraints.NotEmpty;

data class CategoryCreateRequest(
	@NotEmpty
	val category: String
)