package com.programmers.pcquotation.domain.customer.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CustomerInfoResponse {
    private Long id;
    private String username;
    private String customerName;
    private String email;
}