package com.programmers.pcquotation.domain.alarm;

import com.programmers.pcquotation.domain.customer.entity.Customer;
import com.programmers.pcquotation.domain.customer.repository.CustomerRepository;
import com.programmers.pcquotation.domain.estimate.entity.Estimate;
import com.programmers.pcquotation.domain.estimate.repository.EstimateRepository;
import com.programmers.pcquotation.domain.estimaterequest.entity.EstimateRequest;
import com.programmers.pcquotation.domain.seller.entitiy.Seller;
import com.programmers.pcquotation.domain.seller.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlarmService {
	private final EstimateRepository estimateRepository;
	private final CustomerRepository customerRepository;
	private final SellerRepository sellerRepository;
	private final Map<String, SseEmitter> customerSseEmitterMap = new ConcurrentHashMap<>(
			customerRepository.findAll().stream().collect(
					Collectors.toMap(Customer::getCustomerName,customer -> new SseEmitter(Long.MAX_VALUE))
			)
	);
	private final Map<String, SseEmitter> sellerSseEmitterMap = new ConcurrentHashMap<>(
			sellerRepository.findAll().stream().collect(
					Collectors.toMap(Seller::getCompanyName, seller -> new SseEmitter(Long.MAX_VALUE))
			)
	);
	
	public void createEstimateAlarmToCustomer(Estimate estimate) {
		EstimateRequest estimateRequest = estimate.getEstimateRequest();
		String customerName = estimateRequest.getCustomer().getCustomerName();
		if (customerRepository.existsByUsername(customerName)){
			SseEmitter sseEmitterReceiver = customerSseEmitterMap.get(customerName);
			try {
				sseEmitterReceiver.send(SseEmitter.event().name("createEstimate").data("요청하신 견적이 도착했습니다."));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public void createEstimateRequestAlarmToAllSeller() {
			try {
				Collection<SseEmitter> sseEmitterCollectors =  sellerSseEmitterMap.values();
				for(SseEmitter seller : sseEmitterCollectors){
					seller.send(SseEmitter.event().name("createEstimateRequest").data("견적요청이 도착했습니다."));
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
	}
	
	public void adoptAlarmToSeller(Integer estimateId) {
		Estimate estimate = estimateRepository.findById(estimateId).orElseThrow();
		String sellerName = estimate.getSeller().getCompanyName();
		if (sellerRepository.existsByUsername(sellerName)){
			SseEmitter sseEmitterReceiver = sellerSseEmitterMap.get(sellerName);
			try {
				sseEmitterReceiver.send(SseEmitter.event().name("adoptEstimate").data("작성한 견적이 채택됐습니다."));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private void template(){
	
	}
}
