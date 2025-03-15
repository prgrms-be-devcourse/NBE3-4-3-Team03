package com.programmers.pcquotation.domain.chat.service

import com.programmers.pcquotation.domain.chat.dto.ChatMemoryRes
import com.programmers.pcquotation.domain.chat.entity.Chat
import com.programmers.pcquotation.domain.chat.repository.ChatRoomRepository
import com.programmers.pcquotation.domain.estimate.repository.EstimateRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.programmers.pcquotation.domain.chat.repository.ChatRepository as ChatRepository

@Service
class ChatService(
    private val chatRepository: ChatRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val estimateRepository: EstimateRepository
) {
    fun saveChat(username: String, content: String, chatRoomId: Long): Chat {
        val estimate = estimateRepository.getEstimateById(chatRoomId.toInt()) ?: throw NoSuchElementException()
        val chatRoom = chatRoomRepository.findFirstByEstimate(estimate)
            ?: throw NoSuchElementException()
        val chat = Chat(chatRoom, username, content)

        return chatRepository.save(chat)
    }

    fun getChatMemory(chatRoomId: Long): List<ChatMemoryRes> {
        try {
            val estimate = estimateRepository.getEstimateById(chatRoomId.toInt())
            val chatRoom = chatRoomRepository.findFirstByEstimate(estimate)
                ?: return emptyList() // 채팅방이 없으면 빈 리스트 반환

            return chatRepository.findByChatRoom(chatRoom).map { chat ->
                ChatMemoryRes(chat.sender, chat.message, chat.sendDate)
            }

        } catch (e: Exception) {
            return emptyList()
        }
    }

    @Transactional
    fun deleteChat(estimateId: Int) {
        val estimate = estimateRepository.findByIdOrNull(estimateId)
            ?: throw NoSuchElementException("존재하지 않는 견적서입니다.")

        val chatRoom = chatRoomRepository.findFirstByEstimate(estimate) ?: throw NoSuchElementException()
        chatRepository.deleteByChatRoom(chatRoom)
    }
}