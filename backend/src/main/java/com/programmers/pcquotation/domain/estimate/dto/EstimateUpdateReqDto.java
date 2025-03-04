package com.programmers.pcquotation.domain.estimate.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class EstimateUpdateReqDto {
	private Integer estimateId;
	private List<EstimateItemDto> item;
}
