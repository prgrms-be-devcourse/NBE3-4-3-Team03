package com.programmers.pcquotation.domain.estimaterequest.entity

enum class EstimateRequestStatus(private val statusName: String, private val statusCode: Int) {
    WAIT("대기중", 0),
    ADOPT("채택됨", 1)
}