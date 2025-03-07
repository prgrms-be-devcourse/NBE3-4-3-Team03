package com.programmers.pcquotation.domain.estimate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EstimateItemDto {
	private Long item;      // "Intel i5-13500k"
	private Integer price;    // 135000
}