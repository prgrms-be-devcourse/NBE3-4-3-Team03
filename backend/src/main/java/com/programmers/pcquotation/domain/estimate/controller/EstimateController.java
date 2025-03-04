package com.programmers.pcquotation.domain.estimate.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.pcquotation.domain.estimate.dto.EstimateCreateRequest;
import com.programmers.pcquotation.domain.estimate.dto.EstimateSellerResDto;
import com.programmers.pcquotation.domain.estimate.dto.EstimateUpdateReqDto;
import com.programmers.pcquotation.domain.estimate.dto.ReceivedQuoteDTO;
import com.programmers.pcquotation.domain.estimate.service.EstimateService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class EstimateController {
	private final EstimateService estimateService;

	@PostMapping("/api/estimate")
	public ResponseEntity<String> createEstimate(@RequestBody EstimateCreateRequest request, Principal principal) {
		estimateService.createEstimate(request, principal.getName());
		return ResponseEntity.ok().body("");
	}

	@PutMapping("/api/estimate")
	public ResponseEntity<String> updateEstimate(@RequestBody EstimateUpdateReqDto request) {
		estimateService.updateEstimate(request);
		return ResponseEntity.ok().body("");
	}

	@DeleteMapping("/api/estimate/{id}")
	public ResponseEntity<String> deleteEstimate(@PathVariable Integer id) {
		estimateService.deleteEstimate(id);
		return ResponseEntity.ok().body("");
	}

	@GetMapping("/api/estimate/{id}")
	public ResponseEntity<List<ReceivedQuoteDTO>> getEstimateByRequest(@PathVariable Integer id) {
		List<ReceivedQuoteDTO> estimateByRequest = estimateService.getEstimateByRequest(id);
		return new ResponseEntity<>(estimateByRequest, HttpStatus.OK);
	}

	@GetMapping("/api/estimate/seller")
	public ResponseEntity<List<EstimateSellerResDto>> getEstimateByRequest(Principal principal) {
		List<EstimateSellerResDto> estimateBySeller = estimateService.getEstimateBySeller(principal.getName());
		return new ResponseEntity<>(estimateBySeller, HttpStatus.OK);
	}

}