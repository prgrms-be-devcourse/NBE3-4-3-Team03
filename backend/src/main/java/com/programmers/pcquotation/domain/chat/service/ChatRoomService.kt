package com.programmers.pcquotation.domain.chat.service

import com.programmers.pcquotation.domain.chat.entity.ChatRoom
import com.programmers.pcquotation.domain.chat.repository.ChatRoomRepository
import com.programmers.pcquotation.domain.estimate.entity.Estimate
import org.springframework.stereotype.Service

@Service
class ChatRoomService(private val chatRoomRepository: ChatRoomRepository) {

    fun createChatRoom(estimate: Estimate){
        val chatRoom = ChatRoom(estimate)
        chatRoomRepository.save(chatRoom)
    }
    fun deleteChatRoom(estimate: Estimate){
        chatRoomRepository.deleteByEstimate(estimate);
    }
}