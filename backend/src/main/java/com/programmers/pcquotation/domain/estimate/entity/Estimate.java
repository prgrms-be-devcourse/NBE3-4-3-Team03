package com.programmers.pcquotation.domain.estimate.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.programmers.pcquotation.domain.comment.emtity.Comment;
import com.programmers.pcquotation.domain.estimaterequest.entity.EstimateRequest;
import com.programmers.pcquotation.domain.seller.entitiy.Seller;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Estimate {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotNull
	@ManyToOne
	private EstimateRequest estimateRequest;

	@NotNull
	@ManyToOne
	private Seller seller;

	@Setter
	private Integer totalPrice;

	@Setter
	@OneToMany(mappedBy = "estimate", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<EstimateComponent> estimateComponents = new ArrayList<>();

	private LocalDateTime createDate;

	@OneToMany(mappedBy = "estimate", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comment> comments;

	@Builder
	public Estimate(EstimateRequest estimateRequest, Seller seller, Integer totalPrice,
		List<EstimateComponent> estimateComponents) {
		this.estimateRequest = estimateRequest;
		this.seller = seller;
		this.totalPrice = totalPrice;
		this.createDate = LocalDateTime.now();
		this.estimateComponents = estimateComponents;
	}

	public void addEstimateComponent(EstimateComponent component) {
		this.estimateComponents.add(component);
		component.setEstimate(this);
	}

}
