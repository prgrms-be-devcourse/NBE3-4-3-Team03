package com.programmers.pcquotation.domain.estimaterequest.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.programmers.pcquotation.domain.estimaterequest.controller.EstimateRequestController;
import com.programmers.pcquotation.domain.estimaterequest.dto.EstimateRequestData;
import com.programmers.pcquotation.domain.estimaterequest.entity.EstimateRequestStatus;
import com.programmers.pcquotation.domain.estimaterequest.exception.NullEntityException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import com.programmers.pcquotation.domain.customer.entity.Customer;
import com.programmers.pcquotation.domain.customer.repository.CustomerRepository;
import com.programmers.pcquotation.domain.estimaterequest.dto.EstimateRequestResDto;
import com.programmers.pcquotation.domain.estimaterequest.entity.EstimateRequest;
import com.programmers.pcquotation.domain.estimaterequest.repository.EstimateRequestRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class EstimateRequestService {
	private final EstimateRequestRepository estimateRequestRepository;
	private final CustomerRepository customerRepository;

	public EstimateRequest createEstimateRequest(String purpose, Integer budget, String otherRequest,
		Customer customer) {
		return estimateRequestRepository.save(EstimateRequest
			.builder()
			.createDate(LocalDateTime.now())
			.purpose(purpose)
			.budget(budget)
			.otherRequest(otherRequest)
			.customer(customer)
			.status(EstimateRequestStatus.Wait)
			.build());
	}

	public Optional<EstimateRequest> getEstimateRequestById(Integer id) {
		return estimateRequestRepository.getEstimateRequestById(id);
	}

	//로그인 구현시 이 함수로 변경해야함
	public List<EstimateRequestResDto> getEstimateRequestByCustomerId(Customer customer) {
		List<EstimateRequest> allByCustomer = estimateRequestRepository.getAllByCustomer(customer);
		return allByCustomer.stream().map(request -> {
			return EstimateRequestResDto.builder()
				.id(request.getId())
				.purpose(request.getPurpose())
				.budget(request.getBudget())
				.otherRequest(request.getOtherRequest())
				.createDate(request.getCreateDate())
				.status(request.getStatus())
				.build();
		}).toList();
	}

	public Customer findCustomer(String name) {
		return customerRepository.getCustomerByUsername(name)
			.orElseThrow(() -> new NoSuchElementException("고객을 찾을수 없습니다."));
	}

	public List<EstimateRequestResDto> getAllEstimateRequest() {

		List<EstimateRequest> requestRepositoryAll = estimateRequestRepository.findAll();

		return requestRepositoryAll.stream().map(request -> {
			return EstimateRequestResDto.builder()
				.id(request.getId())
				.customerId(request.getCustomer().getCustomerName())
				.purpose(request.getPurpose())
				.budget(request.getBudget())
				.otherRequest(request.getOtherRequest())
				.createDate(request.getCreateDate())
				.status(request.getStatus())
				.build();
		}).toList();
	}

	public void modify(Integer id, EstimateRequestData estimateRequestData) {
		EstimateRequest estimateRequest = estimateRequestRepository
				.findById(id)
				.orElseThrow(NullEntityException::new);
		estimateRequest.UpdateEstimateRequest(estimateRequestData);
	}

	public void Delete(Integer id) {
		estimateRequestRepository.delete(estimateRequestRepository
				.findById(id)
				.orElseThrow(NullEntityException::new));
	}
}
