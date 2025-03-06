package com.programmers.pcquotation.domain.estimate.service

import com.programmers.pcquotation.domain.estimate.dto.*
import com.programmers.pcquotation.domain.estimate.entity.Estimate
import com.programmers.pcquotation.domain.estimate.entity.EstimateComponent
import com.programmers.pcquotation.domain.estimate.entity.EstimateComponent.Companion.createComponent
import com.programmers.pcquotation.domain.estimate.repository.EstimateRepository
import com.programmers.pcquotation.domain.estimaterequest.service.EstimateRequestService
import com.programmers.pcquotation.domain.item.repository.ItemRepository
import com.programmers.pcquotation.domain.seller.service.SellerService
import lombok.AllArgsConstructor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Collectors

@Service
@AllArgsConstructor
open class EstimateService @Autowired constructor(
    private val estimateRepository: EstimateRepository,
    private val estimateRequestService: EstimateRequestService,
    private val sellerService: SellerService,
    private val itemRepository: ItemRepository
) {
    @Transactional
    open fun createEstimate(request: EstimateCreateRequest, sellerName: String) {
        val estimateRequest = estimateRequestService.getEstimateRequestById(request.estimateRequestId)
            .orElseThrow { NoSuchElementException("존재하지 않는 견적 요청입니다.") }

        val seller = sellerService.findByUserName(sellerName)
            .orElseThrow { NoSuchElementException("존재하지 않는 판매자입니다.") }

        val estimate = Estimate(
            estimateRequest = estimateRequest,
            seller = seller,
            totalPrice = getTotalPrice(request.item)
        )

        val components = request.item.stream()
            .map { itemDto: EstimateItemDto ->
                val item = itemRepository.findById(itemDto.item)
                    .orElseThrow { NoSuchElementException("존재하지 않는 아이템입니다.") }
                createComponent(item, itemDto.price, estimate)
            }
            .toList()

        estimate.estimateComponents = components

        estimateRepository.save(estimate)
    }

    fun getTotalPrice(items: List<EstimateItemDto>): Int {
        var total = 0
        for (item in items) {
            total += item.price
        }
        return total
    }

    fun getEstimateByRequest(id: Int?): List<ReceivedQuoteDTO> {
        val list = estimateRepository.getAllByEstimateRequest_Id(id)

        return list.stream().map { quoto: Estimate ->
            ReceivedQuoteDTO.builder()
                .id(quoto.id)
                .seller(quoto.seller.getUsername())
                .date(quoto.createDate)
                .totalPrice(quoto.totalPrice)
                .items(
                    quoto.estimateComponents.stream()
                        .collect(
                            Collectors.toMap(
                                { item: EstimateComponent -> item.item.category.category },
                                { item: EstimateComponent -> item.item.name },
                                { existingValue: String, newValue: String? -> existingValue })
                        )
                )
                .build()
        }.toList()
    }

    fun getEstimateBySeller(username: String?): List<EstimateSellerResDto> {
        val seller = sellerService.findByUserName(username)
            .orElseThrow { NoSuchElementException("존재하지 않는 판매자입니다.") }

        val list = estimateRepository.getAllBySeller(seller)

        return list.stream().map { quoto: Estimate ->
            EstimateSellerResDto.builder()
                .id(quoto.id)
                .purpose(quoto.estimateRequest.purpose)
                .budget(quoto.estimateRequest.budget)
                .customer(quoto.estimateRequest.customer.customerName)
                .date(quoto.estimateRequest.createDate)
                .totalPrice(quoto.totalPrice)
                .items(
                    quoto.estimateComponents.stream()
                        .collect(
                            Collectors.toMap(
                                { estimateComponent -> estimateComponent.item.category.category },
                                { estimateComponent -> estimateComponent.item.name })
                        )
                )
                .build()
        }.toList()
    }

    @Transactional
    open fun deleteEstimate(id: Int) {
        // 견적서가 존재하는지 확인
        val estimate = estimateRepository.findById(id)
            .orElseThrow { NoSuchElementException("존재하지 않는 견적서입니다.") }


        // 연관된 EstimateComponent들 먼저 제거
        estimate.estimateComponents.clear()


        // 견적서 삭제
        estimateRepository.delete(estimate)
    }

    fun updateEstimate(request: EstimateUpdateReqDto) {
        val estimate = estimateRepository.getEstimateById(request.estimateId)

        // 기존 컴포넌트들을 모두 제거
        estimate.estimateComponents.clear()

        // 새로운 총 가격 설정
        estimate.totalPrice = getTotalPrice(request.item)

        // 새로운 컴포넌트들 생성 및 설정
        request.item.stream()
            .forEach { itemDto: EstimateItemDto ->
                val item = itemRepository.findById(itemDto.item)
                    .orElseThrow { NoSuchElementException("존재하지 않는 아이템입니다.") }
                val component = createComponent(item, itemDto.price, estimate)
                estimate.addEstimateComponent(component)
            }

        estimateRepository.save(estimate)
    }
}