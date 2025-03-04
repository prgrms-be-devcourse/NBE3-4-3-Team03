package com.programmers.pcquotation.domain.comment.dto;

import java.time.LocalDateTime;

import com.programmers.pcquotation.domain.comment.emtity.CommentType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CommentCreateRequest(

	@NotNull
	Integer estimateId,

	@NotNull
	Long customerId,

	@NotEmpty
	String content,

	@NotEmpty
	LocalDateTime createDate,

	@NotNull
	CommentType type

) {
}
