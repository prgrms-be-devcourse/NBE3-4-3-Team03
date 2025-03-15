package com.programmers.pcquotation.domain.delivery.service

import com.programmers.pcquotation.domain.delivery.entity.Delivery
import com.programmers.pcquotation.domain.delivery.entity.DeliveryCreateRequest
import com.programmers.pcquotation.domain.delivery.entity.DeliveryDto
import com.programmers.pcquotation.domain.delivery.exception.NullEntityException
import com.programmers.pcquotation.domain.delivery.repository.DeliveryRepository
import com.programmers.pcquotation.domain.estimate.repository.EstimateRepository
import com.programmers.pcquotation.domain.estimaterequest.entity.EstimateRequestStatus
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.NoSuchElementException

@Service
@Transactional
class DeliveryServiceByJpa(
    private val deliveryRepository: DeliveryRepository,
    private val estimateRepository: EstimateRepository
) : DeliveryService {

    //배달 생성을 통해 채택된 상태로 변경
    override fun create(deliveryCreateRequest: DeliveryCreateRequest, id: Int) {
        val estimate = estimateRepository.getEstimateById(id)
        estimate.estimateRequest.updateEstimateRequestStatus(EstimateRequestStatus.ADOPT)

        val delivery = Delivery(estimate, deliveryCreateRequest.address)
        deliveryRepository.save(delivery)
    }

    override fun findAll(): List<DeliveryDto> {
        return deliveryRepository
            .findAll()
            .map { delivery: Delivery -> DeliveryDto(delivery) }
    }

    override fun findByDeliveryId(id: Int): DeliveryDto {
        val delivery = deliveryRepository.findByIdOrNull(id)
            ?: throw NoSuchElementException()
        return DeliveryDto(delivery)
    }

    //배달 삭제 로직을 통해 견적 요청 상태가 초기값으로 돌아가게함
    override fun deleteByDeliveryId(id: Int) {
        val delivery = deliveryRepository.findByIdOrNull(id) ?: throw NullEntityException()
        delivery.estimate.estimateRequest.updateEstimateRequestStatus(EstimateRequestStatus.WAIT)

        deliveryRepository.delete(delivery)
    }

    override fun modify(id: Int, deliveryCreateRequest: DeliveryCreateRequest) {
        val delivery = deliveryRepository.findByIdOrNull(id)
            ?: throw NoSuchElementException()

        delivery.updateAddress(deliveryCreateRequest.address)
    }
}