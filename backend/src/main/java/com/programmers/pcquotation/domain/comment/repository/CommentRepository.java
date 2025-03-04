package com.programmers.pcquotation.domain.comment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.programmers.pcquotation.domain.comment.emtity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findByEstimateId(Long estimateId);
}
