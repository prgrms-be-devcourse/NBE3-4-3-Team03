package com.programmers.pcquotation.domain.delivery.entity;

import lombok.Getter;

@Getter
public enum DeliveryStatus {
    ORDER_COMPLETED("주문 완료", 0),
    IN_DELIVERY("배송 중", 1);

    private final String statusName;
    private final int statusCode;

    DeliveryStatus(String statusName, int statusCode) {
        this.statusName = statusName;
        this.statusCode = statusCode;
    }
}