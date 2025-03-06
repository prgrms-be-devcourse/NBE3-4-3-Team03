package com.programmers.pcquotation.domain.chat.controller

import com.programmers.pcquotation.domain.chat.repository.ChatRoomRepository
import com.programmers.pcquotation.domain.chat.service.ChatRoomService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping

@Controller
class ChatRoomController(private val chatRoomService: ChatRoomService) {


}