package com.programmers.pcquotation.domain.estimate.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class EstimateCreateRequest {
	private Integer estimateRequestId;
	private String sellerId;
	private List<EstimateItemDto> item;
}
