package com.programmers.pcquotation.domain.estimate.service

import com.programmers.pcquotation.domain.estimate.dto.*
import com.programmers.pcquotation.domain.estimate.entity.Estimate
import com.programmers.pcquotation.domain.estimate.entity.EstimateComponent
import com.programmers.pcquotation.domain.estimate.repository.EstimateRepository
import com.programmers.pcquotation.domain.estimaterequest.service.EstimateRequestService
import com.programmers.pcquotation.domain.item.repository.ItemRepository
import com.programmers.pcquotation.domain.seller.entitiy.Seller
import com.programmers.pcquotation.domain.seller.service.SellerService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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
                    items = getItemInfoFromComponents(estimateComponents)
                )
            }
        }
    }

    fun getEstimatesBySeller(id: Int): List<EstimateResponse> {
        val seller = sellerService.findById(id.toLong())
            .orElseThrow { NoSuchElementException("존재하지 않는 판매자입니다.") } as Seller

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
            val item = itemRepository.findById(itemDto.itemId)
                .orElseThrow { NoSuchElementException("존재하지 않는 아이템입니다.") }

            EstimateComponent(
                item = item,
                price = itemDto.price,
                estimate = estimate
            )
        }
    }

    private fun getItemInfoFromComponents(estimateComponents: List<EstimateComponent>): List<ItemInfoDto> {
        return estimateComponents.map { estimateComponent ->
            with(estimateComponent.item) {
                ItemInfoDto(categoryName = category.category, itemName = name)
            }
        }
    }
}