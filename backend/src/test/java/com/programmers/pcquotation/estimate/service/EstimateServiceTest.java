package com.programmers.pcquotation.estimate.service;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// import com.programmers.pcquotation.domain.category.entity.Category;
// import com.programmers.pcquotation.domain.customer.entity.Customer;
// import com.programmers.pcquotation.domain.estimate.dto.EstimateCreateRequest;
// import com.programmers.pcquotation.domain.estimate.dto.EstimateItemDto;
// import com.programmers.pcquotation.domain.estimate.dto.EstimateUpdateReqDto;
// import com.programmers.pcquotation.domain.estimate.entity.Estimate;
// import com.programmers.pcquotation.domain.estimate.repository.EstimateRepository;
// import com.programmers.pcquotation.domain.estimate.service.EstimateService;
// import com.programmers.pcquotation.domain.estimaterequest.entity.EstimateRequest;
// import com.programmers.pcquotation.domain.estimaterequest.entity.EstimateRequestStatus;
// import com.programmers.pcquotation.domain.estimaterequest.service.EstimateRequestService;
// import com.programmers.pcquotation.domain.item.entity.Item;
// import com.programmers.pcquotation.domain.item.service.ItemService;
// import com.programmers.pcquotation.domain.seller.entitiy.Seller;
// import com.programmers.pcquotation.domain.seller.service.SellerService;
// import com.programmers.pcquotation.domain.estimate.dto.EstimateSortType;
// import com.programmers.pcquotation.domain.estimate.dto.EstimateResponse;
//
// import org.junit.jupiter.api.Test;
// import org.mockito.ArgumentCaptor;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageImpl;
// import org.springframework.test.context.ActiveProfiles;
//
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.NoSuchElementException;
// import java.util.Optional;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;
//
@ActiveProfiles("test")
@SpringBootTest
public class EstimateServiceTest {
// 	@InjectMocks
// 	private EstimateService estimateService;
//
// 	@Mock
// 	private EstimateRequestService estimateRequestService;
//
// 	@Mock
// 	private SellerService sellerService;
//
// 	@Mock
// 	private EstimateRepository estimateRepository;
//
// 	@Mock
// 	private ItemService itemService;
//
// 	private final Customer sampleCustomer = new Customer(
// 		1L,
// 		"customer1",
// 		"1234",
// 		"홍길동",
// 		"customer1@test.com",
// 		"좋아하는 음식은?",
// 		"밥",
// 		"api-key"
// 	);
//
// 	private final Seller sampleSeller = new Seller(
// 		1L,
// 		"seller1",
// 		"1234",
// 		"컴퓨터세상",
// 		"seller1@test.com",
// 		"좋아하는 음식은?",
// 		"밥",
// 		true,
// 		"api-key"
// 	);
//
// 	private final EstimateRequest estimateRequest = new EstimateRequest(
// 		1,
// 		"게임용",
// 		1_000_000,
// 		"롤",
// 		LocalDateTime.of(2025, 3, 4, 12, 0, 0),
// 		sampleCustomer,
// 		List.of(),
// 		EstimateRequestStatus.Wait
// 	);
//
// 	private final Item sampleItem1 = new Item(
// 		1L,
// 		"CPU1",
// 		"cpu1.png",
// 		new Category(1L, "CPU", List.of()),
// 		List.of()
// 	);
//
// 	private final Item sampleItem2 = new Item(
// 		2L,
// 		"RAM1",
// 		"ram1.png",
// 		new Category(2L, "RAM", List.of()),
// 		List.of()
// 	);
//
// 	Estimate sampleEstimate = new Estimate(
// 		1,
// 		estimateRequest,
// 		sampleSeller,
// 		5000,
// 		new ArrayList<>(),
// 		LocalDateTime.of(2025, 3, 4, 12, 0),
// 		List.of(),
// 		false
// 	);
//
// 	@Test
// 	public void createEstimate_success() {
// 		when(estimateRequestService.getEstimateRequestById(1)).thenReturn(Optional.of(estimateRequest));
// 		when(sellerService.findByUserName("seller1")).thenReturn(Optional.of(sampleSeller));
// 		when(itemService.findById(1L)).thenReturn(sampleItem1);
// 		when(itemService.findById(2L)).thenReturn(sampleItem2);
// 		when(estimateRepository.save(any(Estimate.class))).thenReturn(sampleEstimate);
//
// 		EstimateCreateRequest request = new EstimateCreateRequest(
// 			1,
// 			List.of(
// 				new EstimateItemDto(1L, 3000),
// 				new EstimateItemDto(2L, 5000)
// 			)
// 		);
//
// 		estimateService.createEstimate(request, "seller1");
//
// 		ArgumentCaptor<Estimate> estimateCaptor = ArgumentCaptor.forClass(Estimate.class);
// 		verify(estimateRepository, times(1)).save(estimateCaptor.capture());
//
// 		Estimate capturedEstimate = estimateCaptor.getValue();
// 		assertNotNull(capturedEstimate);
// 		assertEquals("seller1", capturedEstimate.getSeller().getUsername());
// 		assertEquals(8000, capturedEstimate.getTotalPrice());
// 	}
//
// 	@Test
// 	public void createEstimate_estimateRequestNotFound() {
// 		when(estimateRequestService.getEstimateRequestById(1)).thenReturn(Optional.empty());
//
// 		EstimateCreateRequest request = new EstimateCreateRequest(
// 			1,
// 			List.of(
// 				new EstimateItemDto(1L, 3000),
// 				new EstimateItemDto(2L, 5000)
// 			)
// 		);
//
// 		assertThrows(NoSuchElementException.class, () -> estimateService.createEstimate(request, "seller1"));
// 	}
//
// 	@Test
// 	public void createEstimate_sellerNotFound() {
// 		when(sellerService.findById(1L)).thenReturn(Optional.empty());
//
// 		EstimateCreateRequest request = new EstimateCreateRequest(
// 			1,
// 			List.of(
// 				new EstimateItemDto(1L, 3000),
// 				new EstimateItemDto(2L, 5000)
// 			)
// 		);
//
// 		assertThrows(NoSuchElementException.class, () -> estimateService.createEstimate(request, "seller1"));
// 	}
//
// 	@Test
// 	public void getEstimateByEstimateRequest_Success() {
// 		when(estimateRepository.getAllByEstimateRequestId(1)).thenReturn(List.of(sampleEstimate));
// 		List<EstimateResponse> responses = estimateService.getEstimatesByEstimateRequest(1, EstimateSortType.LATEST);
// 		assertEquals(1, responses.size());
// 	}
//
// 	@Test
// 	public void getEstimatesBySeller_Success() {
// 		when(sellerService.findById(1L)).thenReturn(Optional.of(sampleSeller));
// 		Page<Estimate> estimatePage = new PageImpl<>(List.of(sampleEstimate));
// 		when(estimateRepository.findAllBySeller(eq(sampleSeller), any(PageRequest.class))).thenReturn(estimatePage);
//
// 		Page<EstimateResponse> result = estimateService.getEstimatesBySeller(1, PageRequest.of(0, 10));
// 		assertEquals(1, result.getTotalElements());
// 	}
//
// 	@Test
// 	public void getEstimatesBySeller_SellerNotFound() {
// 		when(sellerService.findById(1L)).thenReturn(Optional.empty());
// 		assertThrows(NoSuchElementException.class, () ->
// 			estimateService.getEstimatesBySeller(1, PageRequest.of(0, 10))
// 		);
// 	}
//
// 	@Test
// 	public void deleteEstimate_Success() {
// 		when(estimateRepository.findById(1)).thenReturn(Optional.of(sampleEstimate));
//
// 		estimateService.deleteEstimate(1);
//
// 		verify(estimateRepository, times(1)).delete(sampleEstimate);
// 	}
//
// 	@Test
// 	public void deleteEstimate_estimateNotFound() {
// 		when(estimateRepository.findById(1)).thenReturn(Optional.empty());
//
// 		assertThrows(NoSuchElementException.class, () -> estimateService.deleteEstimate(1));
// 	}
//
// 	@Test
// 	public void updateEstimate_Success() {
// 		when(estimateRepository.getEstimateById(1)).thenReturn(sampleEstimate);
// 		when(itemService.findById(1L)).thenReturn(sampleItem1);
//
// 		EstimateUpdateReqDto request = new EstimateUpdateReqDto(
// 			1,
// 			List.of(
// 				new EstimateItemDto(1L, 3000)
// 			)
// 		);
//
// 		estimateService.updateEstimate(request);
//
// 		ArgumentCaptor<Estimate> estimateCaptor = ArgumentCaptor.forClass(Estimate.class);
// 		verify(estimateRepository, times(1)).save(estimateCaptor.capture());
//
// 		Estimate capturedEstimate = estimateCaptor.getValue();
// 		assertNotNull(capturedEstimate);
// 		assertEquals("seller1", capturedEstimate.getSeller().getUsername());
// 		assertEquals(3000, capturedEstimate.getTotalPrice());
// 	}
}