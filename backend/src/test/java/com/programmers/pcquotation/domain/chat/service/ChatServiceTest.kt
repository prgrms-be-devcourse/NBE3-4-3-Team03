package com.programmers.pcquotation.domain.chat.service

import com.programmers.pcquotation.domain.chat.entity.Chat
import com.programmers.pcquotation.domain.chat.entity.ChatRoom
import com.programmers.pcquotation.domain.chat.repository.ChatRepository
import com.programmers.pcquotation.domain.chat.repository.ChatRoomRepository
import com.programmers.pcquotation.domain.chat.service.ChatService
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
class ChatServiceTest {
    @Mock
    private lateinit var chatRepository: ChatRepository

    @Mock
    private lateinit var chatRoomRepository: ChatRoomRepository

    @Mock
    private lateinit var estimateRepository: EstimateRepository

    @InjectMocks
    private lateinit var chatService: ChatService

    @Test
    fun saveChat_success() {
        // Given
        val username = "테스트 username"
        val content = "테스트용 메세지 입니다."
        val chatRoomId = 1L

        // 모킹
        val estimate = Mockito.mock(Estimate::class.java)
        val chatRoom = Mockito.mock(ChatRoom::class.java)
        val chat = Mockito.mock(Chat::class.java)

        Mockito.`when`(estimateRepository.getEstimateById(chatRoomId.toInt())).thenReturn(estimate)
        Mockito.`when`(chatRoomRepository.findFirstByEstimate(estimate)).thenReturn(Optional.of(chatRoom))
        Mockito.`when`(chatRepository.save(ArgumentMatchers.any(Chat::class.java))).thenReturn(chat)

        // When
        chatService.saveChat(username, content, chatRoomId)

        // Then
        Mockito.verify(chatRepository).save(ArgumentMatchers.any(Chat::class.java))
    }

    @Test
    fun saveChat_chatRoomNotFound() {
        // Given
        val username = "테스트 username"
        val content = "테스트용 메세지 입니다."
        val chatRoomId = 1L

        // 모킹
        val estimate = Mockito.mock(Estimate::class.java)

        Mockito.`when`(estimateRepository.getEstimateById(chatRoomId.toInt())).thenReturn(estimate)
        Mockito.`when`(chatRoomRepository.findFirstByEstimate(estimate)).thenReturn(Optional.empty())

        // When & Then
        Assertions.assertThrows(
            NoSuchElementException::class.java
        ) {
            chatService.saveChat(username, content, chatRoomId)
        }
    }

    @Test
    fun saveChat_EstimateNotFound() {
        // Given
        val username = "테스트 username"
        val content = "테스트용 메세지 입니다."
        val chatRoomId = 1L

        // 모킹
        Mockito.`when`(estimateRepository.getEstimateById(chatRoomId.toInt())).thenReturn(null)

        // When & Then
        Assertions.assertThrows(
            NoSuchElementException::class.java
        ) {
            chatService.saveChat(username, content, chatRoomId)
        }
    }

    @Test
    fun getChat_success() {
            // Given
            val chatRoomId = 1L

            // 모킹
            val estimate = Mockito.mock(Estimate::class.java)
            val chatRoom = Mockito.mock(ChatRoom::class.java)
            val chat1 = Chat(chatRoom, "user1", "안녕하세요")
            val chat2 = Chat(chatRoom, "user2", "반갑습니다")

            Mockito.`when`(estimateRepository.getEstimateById(chatRoomId.toInt())).thenReturn(estimate)
            Mockito.`when`(chatRoomRepository.findFirstByEstimate(estimate))
                .thenReturn(Optional.of(chatRoom))
            Mockito.`when`<List<Chat>>(chatRepository.findByChatRoom(chatRoom))
                .thenReturn(Arrays.asList(chat1, chat2))

            // When
            val result = chatService.getChatMemory(chatRoomId)

            // Then
            Assertions.assertEquals(2, result.size)
            Assertions.assertEquals("user1", result[0].sender)
            Assertions.assertEquals("안녕하세요", result[0].content)
            Assertions.assertEquals("user2", result[1].sender)
            Assertions.assertEquals("반갑습니다", result[1].content)
        }

    @Test
    fun chat_chatRoomNotFound() {
            // Given
            val chatRoomId = 1L

            // 모킹
            val estimate = Mockito.mock(Estimate::class.java)

            Mockito.`when`(estimateRepository.getEstimateById(chatRoomId.toInt())).thenReturn(estimate)
            Mockito.`when`(chatRoomRepository.findFirstByEstimate(estimate))
                .thenReturn(Optional.empty())

            // When
            val result = chatService.getChatMemory(chatRoomId)

            // Then
            Assertions.assertTrue(result.isEmpty())
        }

    @Test
    fun deleteChat_success() {
        // Given
        val estimateId = 1

        // 모킹
        val estimate = Mockito.mock(Estimate::class.java)
        val chatRoom = Mockito.mock(ChatRoom::class.java)

        Mockito.`when`(estimateRepository.findById(estimateId)).thenReturn(Optional.of(estimate))
        Mockito.`when`(chatRoomRepository.findFirstByEstimate(estimate)).thenReturn(Optional.of(chatRoom))

        // When
        chatService.deleteChat(estimateId)

        // Then
        Mockito.verify(chatRepository).deleteByChatRoom(chatRoom)
    }

    @Test
    fun deleteChat_chatRoomNotFound() {
        // Given
        val estimateId = 1

        // 모킹
        val estimate = Mockito.mock(Estimate::class.java)

        Mockito.`when`(estimateRepository.findById(estimateId)).thenReturn(Optional.of(estimate))
        Mockito.`when`(chatRoomRepository.findFirstByEstimate(estimate)).thenReturn(Optional.empty())

        // When & Then
        Assertions.assertThrows(NoSuchElementException::class.java) {
            chatService.deleteChat(estimateId)
        }
    }

    @Test
    fun deleteChat_estimateNotFound() {
        // Given
        val estimateId = 1

        // 모킹
        Mockito.`when`(estimateRepository.findById(estimateId)).thenReturn(Optional.empty())

        // When & Then
        Assertions.assertThrows(NoSuchElementException::class.java) {
            chatService.deleteChat(estimateId)
        }
    }
}