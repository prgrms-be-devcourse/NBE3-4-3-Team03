package com.programmers.pcquotation.domain.delivery.entity

enum class DeliveryStatus(private val statusName: String, private val statusCode: Int) {
    ORDER_COMPLETED("주문 완료", 0),
    IN_DELIVERY("배송 중", 1)
}