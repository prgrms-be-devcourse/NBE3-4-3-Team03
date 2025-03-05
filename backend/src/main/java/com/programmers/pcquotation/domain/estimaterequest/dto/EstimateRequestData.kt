package com.programmers.pcquotation.domain.estimaterequest.dto;

import jakarta.validation.constraints.NotBlank;

public record EstimateRequestData(@NotBlank String purpose, Integer budget, String otherRequest) {
}
