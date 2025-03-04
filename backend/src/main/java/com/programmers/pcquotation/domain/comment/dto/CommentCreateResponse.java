package com.programmers.pcquotation.domain.comment.dto;

import lombok.NonNull;

public record CommentCreateResponse(
	@NonNull
	Long id,

	@NonNull
	String message
) {
}
