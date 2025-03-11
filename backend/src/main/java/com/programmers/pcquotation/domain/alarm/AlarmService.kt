package com.programmers.pcquotation.domain.alarm

import com.programmers.pcquotation.domain.customer.entity.Customer
import com.programmers.pcquotation.domain.customer.repository.CustomerRepository
import com.programmers.pcquotation.domain.estimate.entity.Estimate
import com.programmers.pcquotation.domain.estimate.repository.EstimateRepository
import com.programmers.pcquotation.domain.estimaterequest.repository.EstimateRequestRepository
import com.programmers.pcquotation.domain.seller.entitiy.Seller
import com.programmers.pcquotation.domain.seller.repository.SellerRepository
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function
import java.util.stream.Collectors

@Service
class AlarmService(
	private val estimateRepository: EstimateRepository,
	private val customerRepository: CustomerRepository,
	private val sellerRepository: SellerRepository,
	private val estimateRequestRepository: EstimateRequestRepository
) {
	
	private val customerSseEmitterMap: Map<String, SseEmitter> = ConcurrentHashMap(
		customerRepository.findAll().stream().collect(
			Collectors.toMap(
				Customer::username,
				Function { customer: Customer -> SseEmitter(Long.MAX_VALUE) })
		)
	)
	private val sellerSseEmitterMap: Map<String, SseEmitter> = ConcurrentHashMap(
		sellerRepository.findAll().stream().collect(
			Collectors.toMap(
				Seller::username,
				Function { seller: Seller -> SseEmitter(Long.MAX_VALUE) })
		)
	)
	
	fun createEstimateAlarmToCustomer(estimate: Estimate) {
		val estimateRequest = estimate.estimateRequest
		val customerName = estimateRequest.customer.username
		if (customerRepository.existsByUsername(customerName.toString())) {
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
		val sellerName = estimate.seller.username
		if (sellerRepository.existsByUsername(sellerName.toString())) {
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
	
	fun getCustomerEmitter(estimateRequestId: Int): SseEmitter {
		val username = estimateRequestRepository.findById(estimateRequestId).orElseThrow().customer.username
		return customerSseEmitterMap[username] ?: throw RuntimeException("Customer not found.")
	}
	
	fun getSellerEmitter(username: String): SseEmitter {
		return sellerSseEmitterMap[username] ?: throw RuntimeException("Seller not found.")
	}
}
