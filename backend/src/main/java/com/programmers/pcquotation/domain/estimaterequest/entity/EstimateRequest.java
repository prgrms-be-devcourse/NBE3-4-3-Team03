package com.programmers.pcquotation.domain.estimaterequest.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.programmers.pcquotation.domain.customer.entity.Customer;

import com.programmers.pcquotation.domain.estimate.entity.Estimate;
import com.programmers.pcquotation.domain.estimaterequest.dto.EstimateRequestData;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstimateRequest {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 20)
	private String purpose;

	@Column(columnDefinition = "INTEGER")
	private Integer budget;

	@Column(length = 200)
	private String otherRequest;

	LocalDateTime createDate;

	@ManyToOne
	private Customer customer;

	@OneToMany(mappedBy = "estimateRequest", cascade = CascadeType.REMOVE)
	private List<Estimate> estimate;

	@Enumerated(EnumType.STRING)
	private EstimateRequestStatus status; // 0: 대기 중, 1: 채택됨

	public void UpdateEstimateRequest(EstimateRequestData estimateRequestData) {
		this.purpose = estimateRequestData.purpose();
		this.budget = estimateRequestData.budget();
		this.otherRequest = estimateRequestData.otherRequest();
	}

	public void UpdateDeliveryStatus(EstimateRequestStatus estimateRequestStatus) {
		this.status = estimateRequestStatus;
	}
}