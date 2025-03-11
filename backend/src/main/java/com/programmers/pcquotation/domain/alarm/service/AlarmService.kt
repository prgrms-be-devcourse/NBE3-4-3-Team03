package com.programmers.pcquotation.domain.alarm.service

import com.programmers.pcquotation.domain.alarm.AlarmEntity
import com.programmers.pcquotation.domain.alarm.AlarmRepository
import com.programmers.pcquotation.domain.estimate.entity.Estimate
import com.programmers.pcquotation.domain.estimate.repository.EstimateRepository
import com.programmers.pcquotation.domain.seller.entitiy.Seller
import com.programmers.pcquotation.domain.seller.repository.SellerRepository
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ConcurrentHashMap

@Service
class AlarmService(
	private val estimateRepository: EstimateRepository,
	private val sellerRepository: SellerRepository,
	private val alarmRepository: AlarmRepository
) {
	val sseEmitterMap: MutableMap<String, SseEmitter> = ConcurrentHashMap()

	fun subscribe(userName: String): SseEmitter {
		val sseEmitter = SseEmitter(Long.MAX_VALUE)
		try {
			sseEmitter.send(SseEmitter.event()
				.name("connect")
				.data("Connected successfully"))
			
			val unreadNotifications = alarmRepository
				.findByReceiverNameAndIsReadOrderByIdDesc(userName, false)
			
			unreadNotifications.forEach { notification ->
				sseEmitter.send(SseEmitter.event()
					.name("unreadMessage")
					.data(notification))
				notification.isRead = true
			}
			alarmRepository.saveAll(unreadNotifications)
			
			sseEmitterMap[userName] = sseEmitter
			
			sseEmitter.onCompletion {
				sseEmitterMap.remove(userName)
			}
			sseEmitter.onTimeout {
				sseEmitter.complete()
				sseEmitterMap.remove(userName)
			}
			sseEmitter.onError { e ->
				sseEmitter.complete()
				sseEmitterMap.remove(userName)
			}
			
		} catch (e: Exception) {
			sseEmitter.completeWithError(e)
			sseEmitterMap.remove(userName)
			throw e
		}
		
		return sseEmitter
	}
	
	fun createEstimateRequestAlarmToAllSeller() {
		val sellerList:List<Seller> = sellerRepository.findAll()
		
		for(seller in sellerList){
			val message = "견적요청이 왔습니다."
			if(sseEmitterMap.containsKey(seller.username)){
				val sseEmitterReceiver: SseEmitter? = sseEmitterMap[seller.username]
				try {
					sseEmitterReceiver?.send(SseEmitter.event().name("addMessage").data(message))
					saveNotification(seller.username.toString(), message)
				} catch (e: Exception) {
					sseEmitterMap.remove(seller.username)
				}
			}
		}
	}
	
	fun adoptAlarmToSeller(estimateId:Int) {
		val estimate:Estimate = estimateRepository.findById(estimateId).orElseThrow()
		
		val sellerName:String = estimate.seller.username.toString()
		val message ="견적이 채댁됐습니다."
		
		if(sseEmitterMap.containsKey(sellerName)){
			val sseEmitterReceiver: SseEmitter? = sseEmitterMap[sellerName]
			try {
				sseEmitterReceiver?.send(SseEmitter.event().name("addMessage").data(message))
				saveNotification(sellerName, message)
			} catch (e: Exception) {
				sseEmitterMap.remove(sellerName)
			}
		}
	}
	
	fun createEstimateAlarmToCustomer(customerName:String) {
		val message ="견적이 생성됐습니다."
		if(sseEmitterMap.containsKey(customerName)){
			val sseEmitterReceiver: SseEmitter? = sseEmitterMap[customerName]
			try {
				sseEmitterReceiver?.send(SseEmitter.event().name("addMessage").data(message))
				saveNotification(customerName, message)
			} catch (e: Exception) {
				sseEmitterMap.remove(customerName)
			}
		}
	}
	private fun saveNotification(receiverName: String, message: String) {
		val alarmEntity = AlarmEntity(
			receiverName = receiverName,
			message = message)
		alarmRepository.save(alarmEntity)
	}
	
	fun getNotifications(username: String): List<AlarmEntity> {
		return alarmRepository.findByReceiverNameAndIsReadOrderByIdDesc(username, false)
	}

	private fun sendNotification(userName: String, message: String) {
		if (sseEmitterMap.containsKey(userName)) {
			try {
				val sseEmitter = sseEmitterMap[userName]
				sseEmitter?.send(SseEmitter.event()
					.name("addMessage")
					.data(AlarmEntity(
						receiverName = userName,
						message = message
					)))
			} catch (e: Exception) {
				sseEmitterMap.remove(userName)
			}
		}
		saveNotification(userName, message)
	}
}