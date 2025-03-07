package com.programmers.pcquotation.domain.chat.service

import com.programmers.pcquotation.domain.chat.entity.ChatRoom
import com.programmers.pcquotation.domain.chat.repository.ChatRoomRepository
import com.programmers.pcquotation.domain.estimate.entity.Estimate
import com.programmers.pcquotation.domain.estimate.repository.EstimateRepository
import org.springframework.stereotype.Service

@Service
class ChatRoomService(
    private val chatRoomRepository: ChatRoomRepository,
    private val estimateRepository: EstimateRepository
) {

    fun createChatRoom(estimate: Estimate) {
        val chatRoom = ChatRoom(estimate)
        chatRoomRepository.save(chatRoom)
    }

    fun deleteChatRoom(estimateId: Int) {
        val estimate = estimateRepository.getEstimateById(estimateId)
        chatRoomRepository.deleteByEstimate(estimate);
    }
}