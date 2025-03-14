package com.programmers.pcquotation.domain.item.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ItemCreateRequest(
	@NotNull
	Long categoryId,
	@NotEmpty
	String name,

	MultipartFile image
) {
}
