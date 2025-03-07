package com.programmers.pcquotation.domain.category.dto;

import jakarta.validation.constraints.NotEmpty;

data class CategoryCreateRequest(
	@field:NotEmpty
	val category: String
)