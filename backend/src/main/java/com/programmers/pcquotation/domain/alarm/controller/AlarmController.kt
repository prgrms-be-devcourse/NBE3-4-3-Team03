package com.programmers.pcquotation.domain.alarm.controller

import com.programmers.pcquotation.domain.alarm.service.AlarmService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/sse")
class AlarmController(
    private val alarmService: AlarmService
) {

    @GetMapping("/login")
    fun subscribe(@AuthenticationPrincipal userDetails: UserDetails): SseEmitter {
        val userName: String = userDetails.username
        val sseEmitter: SseEmitter = alarmService.subscribe(userName)
        return sseEmitter
    }

}