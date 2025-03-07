package com.programmers.pcquotation.domain.estimate.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.pcquotation.domain.estimate.dto.EstimateCreateRequest;
import com.programmers.pcquotation.domain.estimate.dto.EstimateItemDto;
import com.programmers.pcquotation.domain.estimate.dto.EstimateSellerResDto;
import com.programmers.pcquotation.domain.estimate.dto.EstimateUpdateReqDto;
import com.programmers.pcquotation.domain.estimate.dto.ReceivedQuoteDTO;
import com.programmers.pcquotation.domain.estimate.entity.Estimate;
import com.programmers.pcquotation.domain.estimate.entity.EstimateComponent;
import com.programmers.pcquotation.domain.estimate.repository.EstimateRepository;
import com.programmers.pcquotation.domain.estimaterequest.entity.EstimateRequest;
import com.programmers.pcquotation.domain.estimaterequest.service.EstimateRequestService;
import com.programmers.pcquotation.domain.item.entity.Item;
import com.programmers.pcquotation.domain.item.repository.ItemRepository;
import com.programmers.pcquotation.domain.seller.entitiy.Seller;
import com.programmers.pcquotation.domain.seller.service.SellerService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EstimateService {
	private final EstimateRepository estimateRepository;
	private final EstimateRequestService estimateRequestService;
	private final SellerService sellerService;
	private final ItemRepository itemRepository;

	@Transactional
	public void createEstimate(EstimateCreateRequest request, String sellerName) {

		EstimateRequest estimateRequest = estimateRequestService.getEstimateRequestById(request.getEstimateRequestId())
			.orElseThrow(() -> new NoSuchElementException("존재하지 않는 견적 요청입니다."));

		Seller seller = sellerService.findByUserName(sellerName)
			.orElseThrow(() -> new NoSuchElementException("존재하지 않는 판매자입니다."));

		Estimate estimate = Estimate.builder()
			.estimateRequest(estimateRequest)
			.seller(seller)
			.totalPrice(getTotalPrice(request.getItem()))
			.build();

		List<EstimateComponent> components = request.getItem().stream()
			.map(itemDto -> {
				Item item = itemRepository.findById(itemDto.getItem())
					.orElseThrow(() -> new NoSuchElementException("존재하지 않는 아이템입니다."));
				return EstimateComponent.createComponent(item, itemDto.getPrice(), estimate);
			})
			.toList();

		estimate.setEstimateComponents(components);

		estimateRepository.save(estimate);
	}

	public Integer getTotalPrice(List<EstimateItemDto> items) {
		Integer total = 0;
		for (EstimateItemDto item : items) {
			total += item.getPrice();
		}
		return total;
	}

	public List<ReceivedQuoteDTO> getEstimateByRequest(Integer id) {
		List<Estimate> list = estimateRepository.getAllByEstimateRequest_Id(id);

		return list.stream().map(quoto -> {
			return ReceivedQuoteDTO.builder()
				.id(quoto.getId())
				.seller(quoto.getSeller().getUsername())
				.date(quoto.getCreateDate())
				.totalPrice(quoto.getTotalPrice())
				.items(quoto.getEstimateComponents().stream()
					.collect(Collectors.toMap(
						item -> item.getItem().getCategory().getCategory(),
						item -> item.getItem().getName(),
						(existingValue, newValue) -> existingValue)))
				.build();
		}).toList();
	}

	public List<EstimateSellerResDto> getEstimateBySeller(String username) {

		Seller seller = sellerService.findByUserName(username)
			.orElseThrow(() -> new NoSuchElementException("존재하지 않는 판매자입니다."));

		List<Estimate> list = estimateRepository.getAllBySeller(seller);

		return list.stream().map(quoto -> {

			return EstimateSellerResDto.builder()
				.id(quoto.getId())
				.purpose(quoto.getEstimateRequest().getPurpose())
				.budget(quoto.getEstimateRequest().getBudget())
				.customer(quoto.getEstimateRequest().getCustomer().getCustomerName())
				.date(quoto.getEstimateRequest().getCreateDate())
				.totalPrice(quoto.getTotalPrice())
				.items(quoto.getEstimateComponents().stream()
					.collect(Collectors.toMap(
						item -> item.getItem().getCategory().getCategory(),
						item -> item.getItem().getName())))
				.build();
		}).toList();
	}

	@Transactional
	public void deleteEstimate(Integer id) {
		// 견적서가 존재하는지 확인
		Estimate estimate = estimateRepository.findById(id)
			.orElseThrow(() -> new NoSuchElementException("존재하지 않는 견적서입니다."));
		
		// 연관된 EstimateComponent들 먼저 제거
		estimate.getEstimateComponents().clear();
		
		// 견적서 삭제
		estimateRepository.delete(estimate);
	}

	public void updateEstimate(EstimateUpdateReqDto request) {
		Estimate estimateById = estimateRepository.getEstimateById(request.getEstimateId());

		// 기존 컴포넌트들을 모두 제거
		estimateById.getEstimateComponents().clear();

		// 새로운 총 가격 설정
		estimateById.setTotalPrice(getTotalPrice(request.getItem()));

		// 새로운 컴포넌트들 생성 및 설정
		request.getItem().stream()
			.forEach(itemDto -> {
				Item item = itemRepository.findById(itemDto.getItem())
					.orElseThrow(() -> new NoSuchElementException("존재하지 않는 아이템입니다."));
				EstimateComponent component = EstimateComponent.createComponent(item, itemDto.getPrice(), estimateById);
				estimateById.addEstimateComponent(component);
			});

		estimateRepository.save(estimateById);
	}
}