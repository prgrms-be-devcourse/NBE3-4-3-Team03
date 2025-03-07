package com.programmers.pcquotation.domain.delivery.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.programmers.pcquotation.domain.estimate.entity.Estimate
import jakarta.persistence.*


@Entity
class Delivery(
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val estimate: Estimate,

    @Column(length = 50)
    var address: String
){
    constructor() : this(Estimate(), "")

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id:Int = 0

    @Enumerated(EnumType.STRING)
    var status: DeliveryStatus = DeliveryStatus.ORDER_COMPLETED // 0: 주문완료(배송준비중), 1: 배송중

    fun updateAddress(address: String) {
        this.address = address
    }
}

