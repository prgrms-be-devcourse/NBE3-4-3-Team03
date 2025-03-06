package com.programmers.pcquotation.domain.estimate.service

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
    fun createEstimate(request: EstimateCreateRequest, sellerName: String) {
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

        estimateRepository.save(estimate)
    }

    fun getEstimateByRequest(id: Int): List<EstimateForCustomerResponse> {
        val list = estimateRepository.getAllByEstimateRequest_Id(id)

        return list.map { estimate ->
            EstimateForCustomerResponse(
                id = estimate.id,
                companyName = estimate.seller.companyName,
                createdDate = estimate.createDate,
                totalPrice = estimate.totalPrice,
                items = estimate.estimateComponents.stream()
                    .collect(
                        Collectors.toMap(
                            { estimateComponent -> estimateComponent.item.category.category },
                            { estimateComponent -> estimateComponent.item.name },
                            { existingValue: String, newValue: String? -> existingValue })
                    )
            )
        }
    }

    fun getEstimateBySeller(username: String): List<EstimateForSellerResponse> {
        val seller = sellerService.findByUserName(username)
            .orElseThrow { NoSuchElementException("존재하지 않는 판매자입니다.") }

        val list = estimateRepository.getAllBySeller(seller).toList()

        return list.map { estimate ->
            EstimateForSellerResponse(
                id = estimate.id,
                purpose = estimate.estimateRequest.purpose,
                budget = estimate.estimateRequest.budget,
                customerName = estimate.estimateRequest.customer.customerName,
                createdDate = estimate.estimateRequest.createDate,
                totalPrice = estimate.totalPrice,
                items = estimate.estimateComponents.stream()
                    .collect(
                        Collectors.toMap(
                            { estimateComponent -> estimateComponent.item.category.category },
                            { estimateComponent -> estimateComponent.item.name })
                    )
            )
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

        estimate.totalPrice = request.items.sumOf { it.price }
        estimate.deleteEstimateComponents()

        val components = mapItemsToEstimateComponents(estimate, request.items)
        estimate.addEstimateComponents(components)

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