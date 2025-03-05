package com.programmers.pcquotation.domain.estimaterequest.dto;

import java.time.LocalDateTime;

import com.programmers.pcquotation.domain.estimaterequest.entity.EstimateRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter                 // JSON 직렬화를 위해 필요
@Builder
@NoArgsConstructor     // 기본 생성자
@AllArgsConstructor    // 모든 필드를 파라미터로 받는 생성자
public class EstimateRequestResDto {
	private Integer id;
	private String customerId;
	private EstimateRequestStatus status;
	private LocalDateTime createDate;
	private Integer budget;
	private String purpose;
	private String otherRequest;
}
