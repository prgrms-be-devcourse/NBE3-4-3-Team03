package com.programmers.pcquotation.domain.delivery.entity

data class DeliveryDto(val delivery: Delivery) {
    private val id: Int
    private val address: String
    private val estimateId: Int
    private val status: DeliveryStatus

    init {
        this.id = delivery.id
        this.address = delivery.address
        this.estimateId = delivery.estimate.id
        this.status = delivery.status
    }
}
