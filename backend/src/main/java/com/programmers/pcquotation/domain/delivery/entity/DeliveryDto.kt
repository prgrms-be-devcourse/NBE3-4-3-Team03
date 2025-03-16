package com.programmers.pcquotation.domain.delivery.entity

data class DeliveryDto(
    val id: Int,
    val address: String,
    val estimateId: Int,
    val status: DeliveryStatus,
) {
    constructor(delivery: Delivery) : this(
        id = delivery.id,
        address = delivery.address,
        estimateId = delivery.estimate.id,
        status = delivery.status
    )
}