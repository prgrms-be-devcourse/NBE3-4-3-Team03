package com.programmers.pcquotation.domain.estimate.controller;

import java.security.Principal;
import java.util.List;

import com.programmers.pcquotation.domain.alarm.AlarmService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.pcquotation.domain.chat.service.ChatRoomService;
import com.programmers.pcquotation.domain.chat.service.ChatService;
import com.programmers.pcquotation.domain.estimate.dto.EstimateCreateRequest;
import com.programmers.pcquotation.domain.estimate.dto.EstimateForSellerResponse;
import com.programmers.pcquotation.domain.estimate.dto.EstimateUpdateReqDto;
import com.programmers.pcquotation.domain.estimate.dto.EstimateForCustomerResponse;
import com.programmers.pcquotation.domain.estimate.entity.Estimate;
import com.programmers.pcquotation.domain.estimate.service.EstimateService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class EstimateController {
	private final EstimateService estimateService;
	private final ChatService chatService;
	private final ChatRoomService chatRoomService;
	private final AlarmService alarmService;

	@PostMapping("/api/estimate")
	public ResponseEntity<String> createEstimate(@RequestBody EstimateCreateRequest request, Principal principal) {
		Estimate estimate = estimateService.createEstimate(request, principal.getName());
		chatRoomService.createChatRoom(estimate);
		alarmService.createEstimateAlarmToCustomer(estimate);
		return ResponseEntity.ok().body("");
	}

	@PutMapping("/api/estimate")
	public ResponseEntity<String> updateEstimate(@RequestBody EstimateUpdateReqDto request) {
		estimateService.updateEstimate(request);
		return ResponseEntity.ok().body("");
	}

	@DeleteMapping("/api/estimate/{id}")
	public ResponseEntity<String> deleteEstimate(@PathVariable Integer id) {
		chatService.deleteChat(id);
		chatRoomService.deleteChatRoom(id);
		estimateService.deleteEstimate(id);
		return ResponseEntity.ok().body("");
	}

	@GetMapping("/api/estimate/{id}")
	public ResponseEntity<List<EstimateForCustomerResponse>> getEstimateByRequest(@PathVariable Integer id) {
		List<EstimateForCustomerResponse> estimateByRequest = estimateService.getEstimateByRequest(id);
		return new ResponseEntity<>(estimateByRequest, HttpStatus.OK);
	}

	@GetMapping("/api/estimate/seller")
	public ResponseEntity<List<EstimateForSellerResponse>> getEstimateByRequest(Principal principal) {
		List<EstimateForSellerResponse> estimateBySeller = estimateService.getEstimateBySeller(principal.getName());
		return new ResponseEntity<>(estimateBySeller, HttpStatus.OK);
	}

}