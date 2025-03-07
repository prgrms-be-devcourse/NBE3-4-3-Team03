package com.programmers.pcquotation.domain.comment.emtity;

import java.time.LocalDateTime;

import com.programmers.pcquotation.domain.customer.entity.Customer;
import com.programmers.pcquotation.domain.estimate.entity.Estimate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public final class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "estimate_id")
	private Estimate estimate;

	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	// @ManyToOne
	// @JoinColumn(name = "seller_id")
	// private Seller seller;

	private String content;

	private LocalDateTime createDate;

	@Enumerated(EnumType.STRING)
	private CommentType type;

	public void updateComment(String content) {
		this.content = content;
	}
}