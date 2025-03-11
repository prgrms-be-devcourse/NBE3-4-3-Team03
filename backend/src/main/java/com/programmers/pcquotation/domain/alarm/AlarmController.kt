package com.programmers.pcquotation.domain.alarm

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/sse")
class AlarmController (private val alarmService: AlarmService){
	@GetMapping("/customer")
	fun getCustomerEmitter(@RequestParam username: String): SseEmitter {
		return alarmService.getCustomerEmitter(username)
	}
	
	@GetMapping("/seller")
	fun getSellerEmitter(@RequestParam username: String): SseEmitter {
		return alarmService.getSellerEmitter(username)
	}
}