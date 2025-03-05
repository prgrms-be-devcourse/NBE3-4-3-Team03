package com.programmers.pcquotation.domain.delivery.service

import com.programmers.pcquotation.domain.delivery.entity.DeliveryCreateRequest
import com.programmers.pcquotation.domain.delivery.entity.DeliveryDto

interface DeliveryService {
    fun create(deliveryCreateRequest: DeliveryCreateRequest, id: Int)

    fun findAll(): List<DeliveryDto>

    fun findByDeliveryId(id: Int): DeliveryDto

    fun deleteByDeliveryId(id: Int)

    fun modify(id: Int, deliveryCreateRequest:DeliveryCreateRequest)
}