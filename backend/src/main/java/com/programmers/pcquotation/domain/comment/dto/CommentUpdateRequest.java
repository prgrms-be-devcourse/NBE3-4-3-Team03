package com.programmers.pcquotation.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentUpdateRequest(
	@NotBlank
	String content
) {
}
