package com.programmers.pcquotation.domain.chat.service

import com.programmers.pcquotation.domain.chat.dto.ChatMemoryRes
import com.programmers.pcquotation.domain.chat.entity.Chat
import com.programmers.pcquotation.domain.chat.entity.ChatRoom
import com.programmers.pcquotation.domain.chat.repository.ChatRoomRepository
import com.programmers.pcquotation.domain.estimate.entity.Estimate
import com.programmers.pcquotation.domain.estimate.repository.EstimateRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.programmers.pcquotation.domain.chat.repository.ChatRepository as ChatRepository

@Service
class ChatService(private val chatRepository: ChatRepository,
                  private val chatRoomRepository: ChatRoomRepository,
                  private val estimateRepository: EstimateRepository) {

    fun saveChat(username: String, content: String, chatRoomId: Long): Chat {
        val estimate = estimateRepository.getEstimateById(chatRoomId.toInt()) ?:throw NoSuchElementException()
        val chatRoom = chatRoomRepository.findFirstByEstimate(estimate).orElseThrow { throw NoSuchElementException() }
        val chat = Chat(chatRoom, username, content)
        return chatRepository.save(chat)
    }

    fun getChatMemory(chatRoomId: Long): List<ChatMemoryRes> {
        try {
            val estimate = estimateRepository.getEstimateById(chatRoomId.toInt())
            val chatRoomOptional = chatRoomRepository.findFirstByEstimate(estimate)
            
            if (!chatRoomOptional.isPresent) {
                return mutableListOf() // 채팅방이 없으면 빈 리스트 반환
            }

            val chatRoom = chatRoomOptional.get()

            return chatRepository.findByChatRoom(chatRoom).map { chat ->
                ChatMemoryRes(chat.sender, chat.message, chat.sendDate)
            }.toMutableList()

        } catch (e: Exception) {
            return mutableListOf()
        }
    }

    @Transactional
    fun deleteChat(estimateId: Int) {
        val estimate = estimateRepository.findById(estimateId)
            .orElseThrow { NoSuchElementException("존재하지 않는 견적서입니다.") }
        chatRepository.deleteByChatRoom(chatRoomRepository.findFirstByEstimate(estimate).get())
    }


}