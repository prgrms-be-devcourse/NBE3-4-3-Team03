package com.programmers.pcquotation.domain.estimate.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EstimateAllResDto {
	private Integer id;
	private String customerId;
	private Integer budget;
	private String purpose;
	private LocalDateTime createDate;
}
