package com.programmers.pcquotation.domain.chat.controller

import com.programmers.pcquotation.domain.chat.dto.ChatMemoryRes
import com.programmers.pcquotation.domain.chat.dto.ChatMessageReq
import com.programmers.pcquotation.domain.chat.dto.ChatMessageRes
import com.programmers.pcquotation.domain.chat.service.ChatService
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class ChatController(private val chatService: ChatService) {
    @MessageMapping("/chat.{chatRoomId}")
    @SendTo("/sub/chat.{chatRoomId}")
    fun sendMessage(request: ChatMessageReq, @DestinationVariable chatRoomId: Long): ChatMessageRes {
        val chat = chatService.saveChat(request.username, request.content, chatRoomId)
        return ChatMessageRes(request.username, request.content, chat.sendDate)
    }

    @GetMapping("/api/chat/{chatRoomId}")
    @ResponseBody
    fun getChatMemory(@PathVariable("chatRoomId") chatRoomId: Long): List<ChatMemoryRes> {
        return chatService.getChatMemory(chatRoomId)
    }
}