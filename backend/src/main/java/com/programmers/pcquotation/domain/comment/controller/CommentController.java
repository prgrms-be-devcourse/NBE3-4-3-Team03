package com.programmers.pcquotation.domain.comment.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.pcquotation.domain.comment.dto.CommentCreateRequest;
import com.programmers.pcquotation.domain.comment.dto.CommentCreateResponse;
import com.programmers.pcquotation.domain.comment.dto.CommentDeleteResponse;
import com.programmers.pcquotation.domain.comment.dto.CommentInfoResponse;
import com.programmers.pcquotation.domain.comment.dto.CommentUpdateRequest;
import com.programmers.pcquotation.domain.comment.dto.CommentUpdateResponse;
import com.programmers.pcquotation.domain.comment.service.CommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/estimates/comments")
public class CommentController {

	private final CommentService commentService;

	@PostMapping
	public CommentCreateResponse createComment(
		@RequestBody CommentCreateRequest request
	) {
		return commentService.addComment(request);
	}

	@GetMapping
	public List<CommentInfoResponse> getCommentList() {
		return commentService.getCommentList();
	}

	@PutMapping("/{id}")
	public CommentUpdateResponse updateComment(
		@PathVariable Long id,
		@RequestBody CommentUpdateRequest request
	) {
		return commentService.updateComment(id, request);
	}

	@DeleteMapping("/{id}")
	public CommentDeleteResponse deleteComment(
		@PathVariable Long id
	) {
		return commentService.deleteComment(id);
	}

	@GetMapping("/{estimateId}")
	public List<CommentInfoResponse> getCommentsByEstimateId(@PathVariable Long estimateId) {
		return commentService.getCommentsByEstimateId(estimateId);
	}

}
