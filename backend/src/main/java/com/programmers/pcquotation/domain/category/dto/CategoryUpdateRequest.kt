package com.programmers.pcquotation.domain.category.dto;

import jakarta.validation.constraints.NotEmpty;

data class CategoryUpdateRequest(
	@field:NotEmpty
	val category: String
)