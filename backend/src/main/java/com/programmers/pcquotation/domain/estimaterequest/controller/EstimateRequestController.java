package com.programmers.pcquotation.domain.estimaterequest.controller;

import java.security.Principal;
import java.util.List;

import com.programmers.pcquotation.domain.estimaterequest.dto.EstimateRequestData;
import com.programmers.pcquotation.domain.estimaterequest.entity.EstimateRequestStatus;
import com.programmers.pcquotation.global.enums.UserType;
import com.programmers.pcquotation.global.rq.Rq;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.programmers.pcquotation.domain.customer.entity.Customer;
import com.programmers.pcquotation.domain.estimaterequest.dto.EstimateRequestResDto;
import com.programmers.pcquotation.domain.estimaterequest.service.EstimateRequestService;
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
	public ResponseEntity<List<EstimateRequestResDto>> getER(Principal principal) {
		String type = rq.getCookieValue("userType");
		UserType userType =  UserType.fromString(type);
		List<EstimateRequestResDto> list = null;

		switch (userType) {
			case Customer -> {
				Customer customer = estimateRequestService.findCustomer(principal.getName());
				list = estimateRequestService.getEstimateRequestByCustomerId(customer);
			}
			case Seller -> {
				list = estimateRequestService.getAllEstimateRequest();
			}
		}

		return new ResponseEntity<>(list, HttpStatus.OK);
	}
}