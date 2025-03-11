package com.programmers.pcquotation.domain.estimaterequest.service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.programmers.pcquotation.domain.customer.entity.Customer;
import com.programmers.pcquotation.domain.customer.repository.CustomerRepository;
import com.programmers.pcquotation.domain.estimaterequest.dto.EstimateRequestData;
import com.programmers.pcquotation.domain.estimaterequest.dto.EstimateRequestResDto;
import com.programmers.pcquotation.domain.estimaterequest.entity.EstimateRequest;
import com.programmers.pcquotation.domain.estimaterequest.entity.EstimateRequestStatus;
import com.programmers.pcquotation.domain.estimaterequest.exception.NullEntityException;
import com.programmers.pcquotation.domain.estimaterequest.repository.EstimateRequestRepository;

import jakarta.transaction.Transactional;
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

	public Page<EstimateRequestResDto> getEstimateRequestByCustomerId(Customer customer, Pageable pageable) {
		Page<EstimateRequest> requests = estimateRequestRepository.findAllByCustomer(customer, pageable);
		return requests.map(request -> EstimateRequestResDto.builder()
				.id(request.getId())
				.purpose(request.getPurpose())
				.budget(request.getBudget())
				.otherRequest(request.getOtherRequest())
				.createDate(request.getCreateDate())
				.status(request.getStatus())
				.build());
	}

	public Customer findCustomer(String name) {
		return customerRepository.getCustomerByUsername(name)
			.orElseThrow(() -> new NoSuchElementException("고객을 찾을수 없습니다."));
	}

	public Page<EstimateRequestResDto> getAllEstimateRequest(Pageable pageable) {
		Page<EstimateRequest> requests = estimateRequestRepository.findAll(pageable);
		return requests.map(request -> EstimateRequestResDto.builder()
				.id(request.getId())
				.customerId(request.getCustomer().getCustomerName())
				.purpose(request.getPurpose())
				.budget(request.getBudget())
				.otherRequest(request.getOtherRequest())
				.createDate(request.getCreateDate())
				.status(request.getStatus())
				.build());
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
