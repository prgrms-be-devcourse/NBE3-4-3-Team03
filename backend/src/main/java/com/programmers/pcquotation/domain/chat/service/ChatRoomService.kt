package com.programmers.pcquotation.domain.chat.service

import com.programmers.pcquotation.domain.chat.entity.ChatRoom
import com.programmers.pcquotation.domain.chat.repository.ChatRoomRepository
import com.programmers.pcquotation.domain.estimate.entity.Estimate
import com.programmers.pcquotation.domain.estimate.repository.EstimateRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatRoomService(
    private val chatRoomRepository: ChatRoomRepository,
    private val estimateRepository: EstimateRepository
) {

    fun createChatRoom(estimate: Estimate) {
        val chatRoom = ChatRoom(estimate)
        chatRoomRepository.save(chatRoom)
    }

    @Transactional
    fun deleteChatRoom(id: Int) {
        val estimate = estimateRepository.findById(id)
            .orElseThrow { NoSuchElementException("존재하지 않는 견적서입니다.") }
        chatRoomRepository.deleteByEstimate(estimate);
    }
}