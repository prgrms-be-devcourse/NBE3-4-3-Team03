package com.programmers.pcquotation.domain.item.dto;

import jakarta.validation.constraints.NotBlank;

data class ItemInfoResponse(
	@field:NotBlank
	val id: Long,

	@field:NotBlank
		val name: String,

	@field:NotBlank
		val categoryId: Long,

	@field:NotBlank
		val categoryName: String,

	@field:NotBlank
		val filename: String
)
