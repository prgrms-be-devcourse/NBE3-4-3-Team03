package com.programmers.pcquotation.domain.delivery.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.programmers.pcquotation.domain.estimate.entity.Estimate;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    private Estimate estimate;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status; // 0: 주문완료(배송준비중), 1: 배송중

    @Column(length = 50)
    private String address;

    public void updateAddress(String address){
        this.address = address;
    }
}
