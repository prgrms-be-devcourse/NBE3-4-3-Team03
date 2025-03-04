package com.programmers.pcquotation.domain.comment.dto;

import java.time.LocalDateTime;

import com.programmers.pcquotation.domain.comment.emtity.CommentType;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

public record CommentInfoResponse(

	@NonNull
	Long id,

	@NonNull
	Integer estimateId,

	@NonNull
	Long customerId,

	@NotBlank
	String content,

	@NonNull
	LocalDateTime createDate,

	@NonNull
	CommentType type
) {
}
