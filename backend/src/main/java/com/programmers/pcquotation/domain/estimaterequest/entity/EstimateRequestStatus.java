package com.programmers.pcquotation.domain.estimaterequest.entity;

import lombok.Getter;

@Getter
public enum EstimateRequestStatus {
    Wait("대기중", 0),
    Adopt("채택됨", 1);

    private final String statusName;
    private final int statusCode;

    EstimateRequestStatus(String statusName, int statusCode) {
        this.statusName = statusName;
        this.statusCode = statusCode;
    }
}