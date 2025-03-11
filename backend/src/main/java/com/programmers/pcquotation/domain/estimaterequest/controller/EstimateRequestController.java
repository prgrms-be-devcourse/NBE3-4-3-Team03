package com.programmers.pcquotation.domain.estimaterequest.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.pcquotation.domain.customer.entity.Customer;
import com.programmers.pcquotation.domain.estimaterequest.dto.EstimateRequestData;
import com.programmers.pcquotation.domain.estimaterequest.dto.EstimateRequestResDto;
import com.programmers.pcquotation.domain.estimaterequest.service.EstimateRequestService;
import com.programmers.pcquotation.global.enums.UserType;
import com.programmers.pcquotation.global.rq.Rq;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/estimate/request")
@RequiredArgsConstructor
public class EstimateRequestController {
	private final EstimateRequestService estimateRequestService;
	private final Rq rq;

	@PostMapping
	public ResponseEntity<String> createER(@RequestBody @Valid EstimateRequestData estimateRequestData, Principal principal) {
		Customer customer = estimateRequestService.findCustomer(principal.getName());
		estimateRequestService.createEstimateRequest(
			estimateRequestData.purpose(),
			estimateRequestData.budget(),
			estimateRequestData.otherRequest(),
			customer);
		return ResponseEntity.status(HttpStatus.CREATED).body("견적 요청이 생성되었습니다");
	}

	@PutMapping("/{id}")
	public ResponseEntity<String> modifyER(@PathVariable Integer id, @RequestBody @Valid EstimateRequestData estimateRequestData){
		estimateRequestService.modify(id, estimateRequestData);
		return ResponseEntity.status(HttpStatus.OK).body("수정되었습니다");
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> DeleteER(@PathVariable Integer id){
		estimateRequestService.Delete(id);
		return ResponseEntity.status(HttpStatus.OK).body("삭제되었습니다");
	}

	@GetMapping
	public ResponseEntity<Page<EstimateRequestResDto>> getER(
			Principal principal,
			@PageableDefault(size = 5) Pageable pageable
	) {
		String type = rq.getCookieValue("userType");
		UserType userType = UserType.fromString(type);
		Page<EstimateRequestResDto> list = null;

		switch (userType) {
			case CUSTOMER -> {
				Customer customer = estimateRequestService.findCustomer(principal.getName());
				list = estimateRequestService.getEstimateRequestByCustomerId(customer, pageable);
			}
			case SELLER -> {
				list = estimateRequestService.getAllEstimateRequest(pageable);
			}
		}

		return new ResponseEntity<>(list, HttpStatus.OK);
	}
}