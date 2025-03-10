package com.programmers.pcquotation.domain.alarm

import com.programmers.pcquotation.domain.customer.entity.Customer
import com.programmers.pcquotation.domain.customer.repository.CustomerRepository
import com.programmers.pcquotation.domain.estimate.entity.Estimate
import com.programmers.pcquotation.domain.estimate.repository.EstimateRepository
import com.programmers.pcquotation.domain.seller.entitiy.Seller
import com.programmers.pcquotation.domain.seller.repository.SellerRepository
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function
import java.util.stream.Collectors

@Service
@RequiredArgsConstructor
class AlarmService(
	private val estimateRepository: EstimateRepository,
	private val customerRepository: CustomerRepository,
	private val sellerRepository: SellerRepository
) {
	
	private val customerSseEmitterMap: Map<String, SseEmitter> = ConcurrentHashMap(
		customerRepository.findAll().stream().collect(
			Collectors.toMap(
				Customer::customerName,
				Function { customer: Customer -> SseEmitter(Long.MAX_VALUE) })
		)
	)
	private val sellerSseEmitterMap: Map<String, SseEmitter> = ConcurrentHashMap(
		sellerRepository.findAll().stream().collect(
			Collectors.toMap(
				Seller::companyName,
				Function { seller: Seller -> SseEmitter(Long.MAX_VALUE) })
		)
	)
	
	fun createEstimateAlarmToCustomer(estimate: Estimate) {
		val estimateRequest = estimate.estimateRequest
		val customerName = estimateRequest.customer.customerName
		if (customerRepository.existsByUsername(customerName)) {
			val sseEmitterReceiver = customerSseEmitterMap[customerName]
			alarmTemplate {
				sseEmitterReceiver!!.send(
					SseEmitter.event().name("createEstimate").data("요청하신 견적이 도착했습니다.")
				)
			}
		}
	}
	
	fun createEstimateRequestAlarmToAllSeller() {
		val sseEmitterCollectors = sellerSseEmitterMap.values
		alarmTemplate {
			for (seller in sseEmitterCollectors) {
				seller.send(
					SseEmitter.event().name("createEstimateRequest").data("견적요청이 도착했습니다.")
				)
			}
		}
	}
	
	fun adoptAlarmToSeller(estimateId: Int) {
		val estimate = estimateRepository.findById(estimateId).orElseThrow()
		val sellerName = estimate.seller.companyName
		if (sellerRepository.existsByUsername(sellerName)) {
			val sseEmitterReceiver = sellerSseEmitterMap[sellerName]
			alarmTemplate {
				sseEmitterReceiver!!.send(
					SseEmitter.event().name("adopt").data("작성한 견적이 채택됐습니다.")
				)
			}
		}
	}
	
	private fun alarmTemplate(alarmStrategy: AlarmStrategy) {
		try {
			alarmStrategy.doSomething()
		} catch (e: IOException) {
			throw RuntimeException(e)
		}
	}
}
