package com.programmers.pcquotation.domain.chat.service

import com.programmers.pcquotation.domain.chat.entity.ChatRoom
import com.programmers.pcquotation.domain.chat.repository.ChatRoomRepository
import com.programmers.pcquotation.domain.estimate.entity.Estimate
import com.programmers.pcquotation.domain.estimate.repository.EstimateRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.*

@ActiveProfiles("test")
@SpringBootTest
class ChatRoomServiceTest {
    @Mock
    private lateinit var chatRoomRepository: ChatRoomRepository

    @Mock
    private lateinit var estimateRepository: EstimateRepository

    @InjectMocks
    private lateinit var chatRoomService: ChatRoomService

    @Test
    fun createChatRoom_success() {
        // Given

        val estimate = Mockito.mock(Estimate::class.java)

        // When
        chatRoomService.createChatRoom(estimate)

        // Then
        Mockito.verify(chatRoomRepository).save(ArgumentMatchers.any(ChatRoom::class.java))
    }

    @Test
    fun createChatRoom_estimateNotFound() {
        // Given
        val estimate: Estimate? = null

        // When & Then
        Assertions.assertThrows(
            NullPointerException::class.java
        ) {
            chatRoomService.createChatRoom(estimate!!)
        }
    }

    @Test
    fun deleteChatRoom_success() {
        // Given
        val estimateId = 1
        val estimate = Mockito.mock(Estimate::class.java)

        Mockito.`when`(estimateRepository.findById(estimateId)).thenReturn(Optional.of(estimate))

        // When
        chatRoomService.deleteChatRoom(estimateId)

        // Then
        Mockito.verify(chatRoomRepository).deleteByEstimate(estimate)
    }

    @Test
    fun deleteChatRoom_estimateNotFound() {
        // Given
        val estimateId = 1

        // 모킹
        Mockito.`when`(estimateRepository.findById(estimateId)).thenReturn(Optional.empty())

        // When & Then
        Assertions.assertThrows(NoSuchElementException::class.java) {
            chatRoomService.deleteChatRoom(estimateId)
        }
    }
}