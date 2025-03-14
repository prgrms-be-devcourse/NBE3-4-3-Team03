package com.programmers.pcquotation.domain.alarm.service

import com.programmers.pcquotation.domain.estimate.entity.Estimate
import com.programmers.pcquotation.domain.estimate.repository.EstimateRepository
import com.programmers.pcquotation.domain.seller.entity.Seller
import com.programmers.pcquotation.domain.seller.repository.SellerRepository
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

@Service
class AlarmService(
	private val estimateRepository: EstimateRepository,
	private val sellerRepository: SellerRepository
) {
	val sseEmitterMap: MutableMap<String, SseEmitter> = ConcurrentHashMap()

	//최초 연결 로직
	fun subscribe(userName: String): SseEmitter {
		val sseEmitter = SseEmitter(Long.MAX_VALUE)
		try {
			sseEmitter.send(SseEmitter.event().name("connect"))
		} catch (e: IOException) {
			e.printStackTrace()
		}
		sseEmitter.onCompletion { sseEmitterMap.remove(userName) }
		sseEmitter.onTimeout { sseEmitterMap.remove(userName) }
		sseEmitter.onError { e: Throwable? -> sseEmitterMap.remove(userName) }
		
		return sseEmitter
	}
	
	//구매자가 견적요청을 생성하면 판매자에게 알림을보냄
	fun createEstimateRequestAlarmToAllSeller() {
		val sellerList:List<Seller> = sellerRepository.findAll()
		
		for(seller in sellerList){
			if(sseEmitterMap.containsKey(seller.username)){
				val sseEmitterReceiver: SseEmitter? = sseEmitterMap[seller.username]
				try {
					sseEmitterReceiver?.send(SseEmitter.event().name("addMessage").data("견적요청이 왔습니다."))
				} catch (e: Exception) {
					sseEmitterMap.remove(seller.username)
				}
			}
		}
	}
	
	//구매자가 견적을 채택하면 판매자에게 알림을 보냄
	fun adoptAlarmToSeller(estimateId:Int) {
		val estimate:Estimate = estimateRepository.findById(estimateId).orElseThrow()
		val sellerName:String = estimate.seller.username.toString()
		
		if(sseEmitterMap.containsKey(sellerName)){
			val sseEmitterReceiver: SseEmitter? = sseEmitterMap[sellerName]
			try {
				sseEmitterReceiver?.send(SseEmitter.event().name("addMessage").data("견적이 채댁됐습니다."))
			} catch (e: Exception) {
				sseEmitterMap.remove(sellerName)
			}
		}
	}
	
	//판매자가 견적을 작성하면 구매자에게 알림을 보냄
	fun createEstimateAlarmToCustomer(customerName:String) {
		if(sseEmitterMap.containsKey(customerName)){
			val sseEmitterReceiver: SseEmitter? = sseEmitterMap[customerName]
			try {
				sseEmitterReceiver?.send(SseEmitter.event().name("addMessage").data("견적이 생성됐습니다."))
			} catch (e: Exception) {
				sseEmitterMap.remove(customerName)
			}
		}
	}

}