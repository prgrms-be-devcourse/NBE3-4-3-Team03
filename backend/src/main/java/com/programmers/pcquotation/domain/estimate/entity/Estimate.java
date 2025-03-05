package com.programmers.pcquotation.domain.estimate.entity;

import com.programmers.pcquotation.domain.comment.emtity.Comment;
import com.programmers.pcquotation.domain.estimaterequest.entity.EstimateRequest;
import com.programmers.pcquotation.domain.seller.entitiy.Seller;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Estimate {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer id;

	@NotNull
	@ManyToOne
	public EstimateRequest estimateRequest;

	@NotNull
	@ManyToOne
	public Seller seller;

	@Setter
	public  Integer totalPrice;

	@Setter
	@OneToMany(mappedBy = "estimate", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<EstimateComponent> estimateComponents = new ArrayList<>();

	public  LocalDateTime createDate;

	@OneToMany(mappedBy = "estimate", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<Comment> comments;

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
