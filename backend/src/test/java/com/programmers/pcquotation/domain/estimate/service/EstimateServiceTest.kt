package com.programmers.pcquotation.domain.estimate.service

import com.programmers.pcquotation.domain.category.entity.Category
import com.programmers.pcquotation.domain.customer.entity.Customer
import com.programmers.pcquotation.domain.estimate.dto.EstimateCreateRequest
import com.programmers.pcquotation.domain.estimate.dto.EstimateItemDto
import com.programmers.pcquotation.domain.estimate.dto.EstimateSortType
import com.programmers.pcquotation.domain.estimate.dto.EstimateUpdateReqDto
import com.programmers.pcquotation.domain.estimate.entity.Estimate
import com.programmers.pcquotation.domain.estimate.repository.EstimateRepository
import com.programmers.pcquotation.domain.estimate.service.EstimateService
import com.programmers.pcquotation.domain.estimaterequest.entity.EstimateRequest
import com.programmers.pcquotation.domain.estimaterequest.service.EstimateRequestService
import com.programmers.pcquotation.domain.item.entity.Item
import com.programmers.pcquotation.domain.item.service.ItemService
import com.programmers.pcquotation.domain.seller.entity.Seller
import com.programmers.pcquotation.domain.seller.service.SellerService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import java.util.*

@ActiveProfiles("test")
@SpringBootTest
class EstimateServiceTest {
    @Mock
    private lateinit var estimateRequestService: EstimateRequestService

    @Mock
    private lateinit var sellerService: SellerService

    @Mock
    private lateinit var estimateRepository: EstimateRepository

    @Mock
    private lateinit var itemService: ItemService

    @InjectMocks
    private lateinit var estimateService: EstimateService

    private val sampleCustomer = Customer(
        1L,
        "customer1",
        "1234",
        "홍길동",
        "customer1@test.com",
        "좋아하는 음식은?",
        "밥",
        "api-key"
    )

    private val sampleSeller = Seller(
        1L,
        "seller1",
        "1234",
        "컴퓨터세상",
        "seller1@test.com",
        "좋아하는 음식은?",
        "밥",
        true,
        "api-key"
    )

    private val estimateRequest = EstimateRequest(
        "게임용",
        1000000,
        "롤",
        sampleCustomer
    )

    private val sampleItem1 = Item(
        1L,
        "CPU1",
        "cpu1.png",
        Category(1L, "CPU")
    )

    private val sampleItem2 = Item(
        2L,
        "RAM1",
        "ram1.png",
        Category(2L, "RAM")
    )

    private val sampleEstimate: Estimate = Estimate(
        1,
        estimateRequest,
        sampleSeller,
        5000,
        ArrayList(),
        LocalDateTime.of(2025, 3, 4, 12, 0),
        listOf()
    )

    @Test
    fun createEstimate_success() {
        Mockito.`when`(estimateRequestService.getEstimateRequestById(1)).thenReturn(Optional.of(estimateRequest))
        Mockito.`when`(sellerService.findByUserName("seller1")).thenReturn(Optional.of(sampleSeller))
        Mockito.`when`(itemService.findById(1L)).thenReturn(sampleItem1)
        Mockito.`when`(itemService.findById(2L)).thenReturn(sampleItem2)
        Mockito.`when`(estimateRepository.save(ArgumentMatchers.any(Estimate::class.java))).thenReturn(sampleEstimate)

        val request = EstimateCreateRequest(
            1,
            listOf(
                EstimateItemDto(1L, 3000),
                EstimateItemDto(2L, 5000)
            )
        )

        estimateService.createEstimate(request, "seller1")

        val estimateCaptor = ArgumentCaptor.forClass(Estimate::class.java)
        Mockito.verify(estimateRepository, Mockito.times(1)).save(estimateCaptor.capture())

        val capturedEstimate = estimateCaptor.value
        Assertions.assertNotNull(capturedEstimate)
        Assertions.assertEquals("seller1", capturedEstimate.seller.username)
        Assertions.assertEquals(8000, capturedEstimate.totalPrice)
    }

    @Test
    fun createEstimate_estimateRequestNotFound() {
        Mockito.`when`(estimateRequestService.getEstimateRequestById(1)).thenReturn(Optional.empty())

        val request = EstimateCreateRequest(
            1,
            listOf(
                EstimateItemDto(1L, 3000),
                EstimateItemDto(2L, 5000)
            )
        )

        Assertions.assertThrows(
            NoSuchElementException::class.java
        ) { estimateService.createEstimate(request, "seller1") }
    }

    @Test
    fun createEstimate_sellerNotFound() {
        Mockito.`when`(sellerService.findById(1L)).thenReturn(Optional.empty())

        val request = EstimateCreateRequest(
            1,
            listOf(
                EstimateItemDto(1L, 3000),
                EstimateItemDto(2L, 5000)
            )
        )

        Assertions.assertThrows(
            NoSuchElementException::class.java
        ) { estimateService.createEstimate(request, "seller1") }
    }

    @Test
    fun estimateByEstimateRequest_Success() {
            Mockito.`when`(estimateRepository.getAllByEstimateRequestId(1))
                .thenReturn(listOf(sampleEstimate))

            Assertions.assertEquals(
                1,
                estimateService.getEstimatesByEstimateRequest(1, EstimateSortType.LATEST).size
            )
        }

    @Test
    fun getEstimatesBySeller_Success() {
            Mockito.`when`(
                sellerService.findById(
                    1L
                )
            ).thenReturn(Optional.of(sampleSeller))
            Mockito.`when`(
                estimateRepository.findAllBySeller(
                    sampleSeller,
                    Pageable.unpaged()
                )
            ).thenReturn(PageImpl(listOf(sampleEstimate)))

            Assertions.assertEquals(
                1,
                estimateService.getEstimatesBySeller(1, Pageable.unpaged()).size
            )
        }

    @Test
    fun getEstimatesBySeller_SellerNotFound() {
            Mockito.`when`(sellerService.findById(1L)).thenReturn(Optional.empty())

            Assertions.assertThrows(NoSuchElementException::class.java) {
                estimateService.getEstimatesBySeller(1, Pageable.unpaged())
            }
        }

    @Test
    fun deleteEstimate_Success() {
        Mockito.`when`(estimateRepository.findById(1)).thenReturn(Optional.of(sampleEstimate))

        estimateService.deleteEstimate(1)

        Mockito.verify(estimateRepository, Mockito.times(1)).delete(sampleEstimate)
    }

    @Test
    fun deleteEstimate_estimateNotFound() {
        Mockito.`when`(estimateRepository.findById(1)).thenReturn(Optional.empty())

        Assertions.assertThrows(
            NoSuchElementException::class.java
        ) { estimateService.deleteEstimate(1) }
    }

    @Test
    fun updateEstimate_Success() {
        Mockito.`when`(estimateRepository.getEstimateById(1)).thenReturn(sampleEstimate)
        Mockito.`when`(itemService.findById(1L)).thenReturn(sampleItem1)

        val request = EstimateUpdateReqDto(
            1,
            listOf(
                EstimateItemDto(1L, 3000)
            )
        )

        estimateService.updateEstimate(request)

        val estimateCaptor = ArgumentCaptor.forClass(Estimate::class.java)
        Mockito.verify(estimateRepository, Mockito.times(1)).save(estimateCaptor.capture())

        val capturedEstimate = estimateCaptor.value
        Assertions.assertNotNull(capturedEstimate)
        Assertions.assertEquals("seller1", capturedEstimate.seller.username)
        Assertions.assertEquals(3000, capturedEstimate.totalPrice)
    }
}