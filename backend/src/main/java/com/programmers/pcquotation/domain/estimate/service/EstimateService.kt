package com.programmers.pcquotation.domain.estimate.service

import com.programmers.pcquotation.domain.alarm.service.AlarmService
import com.programmers.pcquotation.domain.estimate.dto.*
import com.programmers.pcquotation.domain.estimate.entity.Estimate
import com.programmers.pcquotation.domain.estimate.entity.EstimateComponent
import com.programmers.pcquotation.domain.estimate.repository.EstimateRepository
import com.programmers.pcquotation.domain.estimaterequest.service.EstimateRequestService
import com.programmers.pcquotation.domain.item.service.ItemService
import com.programmers.pcquotation.domain.seller.entity.Seller
import com.programmers.pcquotation.domain.seller.service.SellerService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EstimateService(
    private val estimateRepository: EstimateRepository,
    private val estimateRequestService: EstimateRequestService,
    private val sellerService: SellerService,
    private val itemService: ItemService
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

    fun getEstimatesByEstimateRequest(estimateRequestId: Int, sortType: EstimateSortType = EstimateSortType.LATEST): List<EstimateResponse> {
        val estimates = estimateRepository.getAllByEstimateRequestId(estimateRequestId)

        val estimateResponses = estimates.map { estimate ->
            with(estimate) {
                EstimateResponse(
                    id = estimate.id,
                    purpose = estimateRequest.purpose,
                    budget = estimateRequest.budget,
                    customerName = estimateRequest.customer.customerName,
                    companyName = seller.companyName,
                    createdDate = createDate,
                    totalPrice = totalPrice,
                    items = getItemInfoFromComponents(estimateComponents)
                )
            }
        }

        return when (sortType) {
            EstimateSortType.LATEST -> estimateResponses.sortedByDescending { it.createdDate }
            EstimateSortType.PRICE_ASC -> estimateResponses.sortedBy { it.totalPrice }
            EstimateSortType.PRICE_DESC -> estimateResponses.sortedByDescending { it.totalPrice }
        }
    }

    fun getEstimatesBySeller(id: Int, pageable: Pageable): Page<EstimateResponse> {
        val seller = sellerService.findById(id.toLong())
            .orElseThrow { NoSuchElementException("존재하지 않는 판매자입니다.") } as Seller

        return estimateRepository.findAllBySeller(seller, pageable).map { estimate ->
            with(estimate) {
                EstimateResponse(
                    id = estimate.id,
                    purpose = estimateRequest.purpose,
                    budget = estimateRequest.budget,
                    customerName = estimateRequest.customer.customerName,
                    companyName = seller.companyName,
                    createdDate = estimateRequest.createDate,
                    totalPrice = totalPrice,
                    items = getItemInfoFromComponents(estimateComponents)
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
            val item = itemService.findById(itemDto.itemId)

            EstimateComponent(
                item = item,
                price = itemDto.price,
                estimate = estimate
            )
        }
    }

    private fun getItemInfoFromComponents(components: List<EstimateComponent>): List<ItemInfoDto> {
        return components.map { component ->
            with(component.item) {
                ItemInfoDto(
                    categoryName = category.category,
                    itemName = name
                )
            }
        }
    }
}