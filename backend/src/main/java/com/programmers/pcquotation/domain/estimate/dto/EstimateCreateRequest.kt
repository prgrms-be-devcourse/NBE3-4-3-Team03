package com.programmers.pcquotation.domain.estimate.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EstimateCreateRequest {
	private Integer estimateRequestId;
	private List<EstimateItemDto> item;
}