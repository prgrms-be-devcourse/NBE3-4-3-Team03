package com.programmers.pcquotation.domain.delivery.entity;

import lombok.Getter;

@Getter
public class DeliveryDto {
    private Integer id;
    private String address;
    private Integer estimateId;
    private DeliveryStatus status;

    public DeliveryDto(Delivery delivery){
        this.id = delivery.getId();
        this.address = delivery.getAddress();
        this.estimateId = delivery.getEstimate().getId();
        this.status = delivery.getStatus();
    }
}
