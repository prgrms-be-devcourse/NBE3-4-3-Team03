package com.programmers.pcquotation.domain.delivery.service

import com.programmers.pcquotation.domain.delivery.entity.Delivery
import com.programmers.pcquotation.domain.delivery.entity.DeliveryCreateRequest
import com.programmers.pcquotation.domain.delivery.entity.DeliveryDto
import com.programmers.pcquotation.domain.delivery.entity.DeliveryStatus
import com.programmers.pcquotation.domain.delivery.exception.NullEntityException
import com.programmers.pcquotation.domain.delivery.repository.DeliveryRepository
import com.programmers.pcquotation.domain.estimate.repository.EstimateRepository
import com.programmers.pcquotation.domain.estimaterequest.entity.EstimateRequestStatus
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
open class DeliveryServiceByJpa(
    private val deliveryRepository: DeliveryRepository,
    private val estimateRepository: EstimateRepository
):DeliveryService{

    //배달 생성을 통해 채택된 상태로 변경
    override fun create(deliveryCreateRequest:DeliveryCreateRequest, id: Int) {
        val delivery = Delivery(
            estimateRepository.getEstimateById(id),
            DeliveryStatus.ORDER_COMPLETED,
            deliveryCreateRequest.address
        )
        deliveryRepository.save(delivery)
    }

    override fun findAll(): List<DeliveryDto> {
        if (deliveryRepository.findAll().isNotEmpty()) {
            return deliveryRepository
                .findAll()
                .stream()
                .map { delivery: Delivery -> DeliveryDto(delivery) }
                .toList()
        } else throw NullEntityException()
    }

    override fun findByDeliveryId(id: Int): DeliveryDto {
        return deliveryRepository
            .findById(id)
            .stream()
            .map { delivery: Delivery -> DeliveryDto(delivery) }
            .findAny()
            .orElseThrow { NullEntityException() }
    }

    //배달 삭제 로직을 통해 견적 요청 상태가 초기값으로 돌아가게함
    override fun deleteByDeliveryId(id: Int) {
        val delivery = deliveryRepository.findById(id).orElseThrow { NullEntityException() }
        delivery.estimate.estimateRequest.updateDeliveryStatus(EstimateRequestStatus.Wait)
        deliveryRepository.delete(delivery)
    }

    override fun modify(id: Int, deliveryCreateRequest:DeliveryCreateRequest) {
        deliveryRepository
            .findById(id)
            .orElseThrow { NullEntityException() }
            .updateAddress(deliveryCreateRequest.address)
    }
}
