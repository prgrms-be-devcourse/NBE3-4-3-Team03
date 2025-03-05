package com.programmers.pcquotation.domain.estimate.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EstimateUpdateReqDto {
	private Integer estimateId;
	private List<EstimateItemDto> item;
}