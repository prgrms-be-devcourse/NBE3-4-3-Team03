package com.programmers.pcquotation.domain.estimate.service

import com.programmers.pcquotation.domain.chat.repository.ChatRepository
import com.programmers.pcquotation.domain.chat.repository.ChatRoomRepository
import com.programmers.pcquotation.domain.chat.service.ChatService
import com.programmers.pcquotation.domain.estimate.dto.*
import com.programmers.pcquotation.domain.estimate.entity.Estimate
import com.programmers.pcquotation.domain.estimate.entity.EstimateComponent
import com.programmers.pcquotation.domain.estimate.repository.EstimateRepository
import com.programmers.pcquotation.domain.estimaterequest.service.EstimateRequestService
import com.programmers.pcquotation.domain.item.repository.ItemRepository
import com.programmers.pcquotation.domain.seller.service.SellerService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Collectors

@Service
class EstimateService(
    private val estimateRepository: EstimateRepository,
    private val estimateRequestService: EstimateRequestService,
    private val sellerService: SellerService,
    private val itemRepository: ItemRepository
) {
    @Transactional
    fun createEstimate(request: EstimateCreateRequest, sellerName: String): Estimate {
        val estimateRequest = estimateRequestService.getEstimateRequestById(request.estimateRequestId)
            .orElseThrow { NoSuchElementException("존재하지 않는 견적 요청입니다.") }

        val seller = sellerService.findByUserName(sellerName)
            .orElseThrow { NoSuchElementException("존재하지 않는 판매자입니다.") }

        val estimate = Estimate(
            estimateRequest = estimateRequest,
            seller = seller,
            totalPrice = request.items.sumOf { it.price }
        )

        val components = mapItemsToEstimateComponents(estimate, request.items)
        estimate.addEstimateComponents(components)

        return estimateRepository.save(estimate)
    }

    fun getEstimatesByEstimateRequest(estimateRequestId: Int): List<EstimateResponse> {
        val estimates = estimateRepository.getAllByEstimateRequestId(estimateRequestId)

        return estimates.map { estimate ->
            with(estimate) {
                EstimateResponse(
                    id = estimate.id,
                    purpose = estimateRequest.purpose,
                    budget = estimateRequest.budget,
                    customerName = estimateRequest.customer.customerName,
                    companyName = seller.companyName,
                    createdDate = createDate,
                    totalPrice = totalPrice,
                    items = estimateComponents.stream()
                        .collect(
                            Collectors.toMap(
                                { estimateComponent -> estimateComponent.item.category.category },
                                { estimateComponent -> estimateComponent.item.name },
                                { existingValue: String, newValue: String? -> existingValue })
                        )
                )
            }
        }
    }

    fun getEstimatesBySeller(username: String): List<EstimateResponse> {
        val seller = sellerService.findByUserName(username)
            .orElseThrow { NoSuchElementException("존재하지 않는 판매자입니다.") }

        val estimates = estimateRepository.getAllBySeller(seller)

        return estimates.map { estimate ->
            with(estimate) {
                EstimateResponse(
                    id = id,
                    purpose = estimateRequest.purpose,
                    budget = estimateRequest.budget,
                    customerName = estimateRequest.customer.customerName,
                    companyName = seller.companyName,
                    createdDate = estimateRequest.createDate,
                    totalPrice = totalPrice,
                    items = estimateComponents.stream()
                        .collect(
                            Collectors.toMap(
                                { estimateComponent -> estimateComponent.item.category.category },
                                { estimateComponent -> estimateComponent.item.name })
                        )
                )
            }
        }
    }

    @Transactional
    fun deleteEstimate(id: Int) {
        val estimate = estimateRepository.findById(id)
            .orElseThrow { NoSuchElementException("존재하지 않는 견적서입니다.") }

        estimate.deleteEstimateComponents()
        estimateRepository.delete(estimate)
    }

    fun updateEstimate(request: EstimateUpdateReqDto) {
        val estimate = estimateRepository.getEstimateById(request.estimateId)

        estimate.apply {
            deleteEstimateComponents()

            val components = mapItemsToEstimateComponents(this, request.items)
            addEstimateComponents(components)

            totalPrice = request.items.sumOf { it.price }
        }

        estimateRepository.save(estimate)
    }

    private fun mapItemsToEstimateComponents(
        estimate: Estimate,
        items: List<EstimateItemDto>
    ): List<EstimateComponent> {
        return items.map { itemDto ->
            val item = itemRepository.findById(itemDto.itemId)
                .orElseThrow { NoSuchElementException("존재하지 않는 아이템입니다.") }

            EstimateComponent(
                item = item,
                price = itemDto.price,
                estimate = estimate
            )
        }
    }
}