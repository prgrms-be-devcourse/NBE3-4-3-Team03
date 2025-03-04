package com.programmers.pcquotation.domain.estimate.dto;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstimateSellerResDto {
	private Integer id;                //견적 ID
	private String purpose;            // 용도
	private Integer budget;               // 예산
	private String customer;           // 요청자 이름
	private LocalDateTime date;        // 견적 요청일
	private Integer totalPrice;        // 총 견적 금액
	private Map<String, String> items; // 부품 목록 (예: {"cpu": "Intel i5-13400F", ...})
}
