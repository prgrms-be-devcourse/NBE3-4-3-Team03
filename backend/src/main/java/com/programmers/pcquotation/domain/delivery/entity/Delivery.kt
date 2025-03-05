package com.programmers.pcquotation.domain.delivery.entity

import com.programmers.pcquotation.domain.estimate.entity.Estimate
import com.programmers.pcquotation.domain.estimaterequest.entity.EstimateRequest
import com.programmers.pcquotation.domain.seller.entitiy.Seller
import jakarta.persistence.*


@Entity
class Delivery(
    @OneToOne(fetch = FetchType.LAZY)
    val estimate: Estimate,

    @Enumerated(EnumType.STRING)
    var status: DeliveryStatus, // 0: 주문완료(배송준비중), 1: 배송중

    @Column(length = 50)
    var address: String
){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id:Int = 0

    constructor() : this(Estimate(
        EstimateRequest(), Seller(), 0, emptyList()
    ), DeliveryStatus.ORDER_COMPLETED, "")

    fun updateAddress(address: String) {
        this.address = address
    }
}

