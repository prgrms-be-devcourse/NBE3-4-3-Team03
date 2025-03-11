package com.programmers.pcquotation.domain.estimaterequest.entity

enum class EstimateRequestStatus(private val statusName: String, private val statusCode: Int) {
    Wait("대기중", 0),
    Adopt("채택됨", 1)
}