package com.programmers.pcquotation.estimate.service;

import com.programmers.pcquotation.domain.category.entity.Category;
import com.programmers.pcquotation.domain.customer.entity.Customer;
import com.programmers.pcquotation.domain.estimate.dto.EstimateCreateRequest;
import com.programmers.pcquotation.domain.estimate.dto.EstimateItemDto;
import com.programmers.pcquotation.domain.estimate.entity.Estimate;
import com.programmers.pcquotation.domain.estimate.repository.EstimateRepository;
import com.programmers.pcquotation.domain.estimate.service.EstimateService;
import com.programmers.pcquotation.domain.estimaterequest.entity.EstimateRequest;
import com.programmers.pcquotation.domain.estimaterequest.entity.EstimateRequestStatus;
import com.programmers.pcquotation.domain.estimaterequest.service.EstimateRequestService;
import com.programmers.pcquotation.domain.item.entity.Item;
import com.programmers.pcquotation.domain.item.repository.ItemRepository;
import com.programmers.pcquotation.domain.seller.entitiy.Seller;
import com.programmers.pcquotation.domain.seller.service.SellerService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class EstimateServiceTest {
    @Autowired
    private EstimateService estimateService;

    @MockitoBean
    private EstimateRequestService estimateRequestService;

    @MockitoBean
    private SellerService sellerService;

    @MockitoBean
    private EstimateRepository estimateRepository;

    @MockitoBean
    private ItemRepository itemRepository;

    private final EstimateRequest estimateRequest = new EstimateRequest(
            1,
            "게임용",
            1_000_000,
            "롤",
            LocalDateTime.of(2025, 3, 4, 12, 0, 0),
            mock(Customer.class),
            List.of(),
            EstimateRequestStatus.Wait
    );

    private final Seller sampleSeller = new Seller(
            1L,
            "seller1",
            "1234",
            "컴퓨터세상",
            "seller1@test.com",
            "좋아하는 음식은?",
            "밥",
            true,
            "api-key"
    );

    private final Item sampleItem1 = new Item(
            1L,
            "CPU1",
            "cpu1.png",
            new Category(1L, "CPU"),
            List.of()
    );

    private final Item sampleItem2 = new Item(
            2L,
            "RAM1",
            "ram1.png",
            new Category(2L, "RAM"),
            List.of()
    );

    Estimate sampleEstimate = new Estimate(
            1,
            estimateRequest,
            sampleSeller,
            5000,
            new ArrayList<>(),
            LocalDateTime.of(2025, 3, 4, 12, 0),
            List.of()
    );

    @Test
    public void createEstimate_success() {
        when(estimateRequestService.getEstimateRequestById(1)).thenReturn(Optional.of(estimateRequest));
        when(sellerService.findByUserName("seller1")).thenReturn(Optional.of(sampleSeller));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem1));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(sampleItem2));

        EstimateCreateRequest request = new EstimateCreateRequest(
                1,
                1,
                List.of(
                        new EstimateItemDto(1L, 3000),
                        new EstimateItemDto(2L, 5000)
                )
        );

        estimateService.createEstimate(request, "seller1");

        ArgumentCaptor<Estimate> estimateCaptor = ArgumentCaptor.forClass(Estimate.class);
        verify(estimateRepository, times(1)).save(estimateCaptor.capture());

        Estimate capturedEstimate = estimateCaptor.getValue();
        assertNotNull(capturedEstimate);
        assertEquals("seller1", capturedEstimate.getSeller().getUsername());
        assertEquals(8000, capturedEstimate.getTotalPrice());
    }

    @Test
    public void createEstimate_estimateRequestNotFound() {
        when(estimateRequestService.getEstimateRequestById(1)).thenReturn(Optional.empty());

        EstimateCreateRequest request = new EstimateCreateRequest(
                1,
                1,
                List.of(
                        new EstimateItemDto(1L, 3000),
                        new EstimateItemDto(2L, 5000)
                )
        );

        assertThrows(NoSuchElementException.class, () -> estimateService.createEstimate(request, "seller1"));
    }

    @Test
    public void createEstimate_sellerNotFound() {
        when(sellerService.findByUserName("seller1")).thenReturn(Optional.empty());

        EstimateCreateRequest request = new EstimateCreateRequest(
                1,
                1,
                List.of(
                        new EstimateItemDto(1L, 3000),
                        new EstimateItemDto(2L, 5000)
                )
        );

        assertThrows(NoSuchElementException.class, () -> estimateService.createEstimate(request, "seller1"));
    }

    @Test
    public void getEstimateByEstimateRequest_Success() {
        when(estimateRepository.getAllByEstimateRequest_Id(1)).thenReturn(List.of(sampleEstimate));

        assertEquals(1, estimateService.getEstimateByRequest(1).size());
    }

    @Test
    public void getEstimateBySeller_Success() {
        when(sellerService.findByUserName("seller1")).thenReturn(Optional.of(sampleSeller));
        when(estimateRepository.getAllBySeller(sampleSeller)).thenReturn(List.of(sampleEstimate));

        assertEquals(1, estimateService.getEstimateBySeller("seller1").size());
    }

    @Test
    public void getEstimateBySeller_SellerNotFound() {
        when(sellerService.findByUserName("seller1")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> estimateService.getEstimateBySeller("seller1"));
    }

    @Test
    public void deleteEstimate_Success() {
        when(estimateRepository.findById(1)).thenReturn(Optional.of(sampleEstimate));

        estimateService.deleteEstimate(1);

        verify(estimateRepository, times(1)).delete(sampleEstimate);
    }
}