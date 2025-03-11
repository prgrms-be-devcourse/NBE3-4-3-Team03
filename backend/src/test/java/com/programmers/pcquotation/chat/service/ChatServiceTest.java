package com.programmers.pcquotation.chat.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.test.context.ActiveProfiles;

import com.programmers.pcquotation.domain.chat.entity.Chat;
import com.programmers.pcquotation.domain.chat.entity.ChatRoom;
import com.programmers.pcquotation.domain.chat.repository.ChatRepository;
import com.programmers.pcquotation.domain.chat.repository.ChatRoomRepository;
import com.programmers.pcquotation.domain.chat.service.ChatService;
import com.programmers.pcquotation.domain.estimate.entity.Estimate;
import com.programmers.pcquotation.domain.estimate.repository.EstimateRepository;


@ActiveProfiles("test")
@SpringBootTest
public class ChatServiceTest {

	@Mock
	private ChatRepository chatRepository;

	@Mock
	private ChatRoomRepository chatRoomRepository;

	@Mock
	private EstimateRepository estimateRepository;
	
	@InjectMocks
	private ChatService chatService;

	@Test
	public void saveChat_success(){
		// Given
		String username = "테스트 username";
		String content = "테스트용 메세지 입니다.";
		long chatRoomId = 1L;
		
		// 모킹
		Estimate estimate = mock(Estimate.class);
		ChatRoom chatRoom = mock(ChatRoom.class);
		Chat chat = mock(Chat.class);
		
		when(estimateRepository.getEstimateById((int)chatRoomId)).thenReturn(estimate);
		when(chatRoomRepository.findFirstByEstimate(estimate)).thenReturn(Optional.of(chatRoom));
		when(chatRepository.save(any(Chat.class))).thenReturn(chat);
		
		// When
		chatService.saveChat(username, content, chatRoomId);
		
		// Then
		verify(chatRepository).save(any(Chat.class));
	}

	@Test
	public void saveChat_chatRoomNotFound(){
		// Given
		String username = "테스트 username";
		String content = "테스트용 메세지 입니다.";
		long chatRoomId = 1L;
		
		// 모킹
		Estimate estimate = mock(Estimate.class);

		when(estimateRepository.getEstimateById((int)chatRoomId)).thenReturn(estimate);
		when(chatRoomRepository.findFirstByEstimate(estimate)).thenReturn(Optional.empty());

		// When & Then
		assertThrows(NoSuchElementException.class, () -> {
			chatService.saveChat(username, content, chatRoomId);
		});
	}

	@Test
	public void saveChat_EstimateNotFound(){
		// Given
		String username = "테스트 username";
		String content = "테스트용 메세지 입니다.";
		long chatRoomId = 1L;

		// 모킹
		when(estimateRepository.getEstimateById((int)chatRoomId)).thenReturn(null);

		// When & Then
		assertThrows(NoSuchElementException.class, () -> {
			chatService.saveChat(username, content, chatRoomId);
		});
	}

	@Test
	public void getChat_success(){
		// Given
		long chatRoomId = 1L;
		
		// 모킹
		Estimate estimate = mock(Estimate.class);
		ChatRoom chatRoom = mock(ChatRoom.class);
		Chat chat1 = new Chat(chatRoom, "user1", "안녕하세요");
		Chat chat2 = new Chat(chatRoom, "user2", "반갑습니다");
		
		when(estimateRepository.getEstimateById((int)chatRoomId)).thenReturn(estimate);
		when(chatRoomRepository.findFirstByEstimate(estimate)).thenReturn(Optional.of(chatRoom));
		when(chatRepository.findByChatRoom(chatRoom)).thenReturn(java.util.Arrays.asList(chat1, chat2));
		
		// When
		var result = chatService.getChatMemory(chatRoomId);
		
		// Then
		assertEquals(2, result.size());
		assertEquals("user1", result.get(0).getSender());
		assertEquals("안녕하세요", result.get(0).getContent());
		assertEquals("user2", result.get(1).getSender());
		assertEquals("반갑습니다", result.get(1).getContent());
	}
	
	@Test
	public void getChat_chatRoomNotFound(){
		// Given
		long chatRoomId = 1L;
		
		// 모킹
		Estimate estimate = mock(Estimate.class);
		
		when(estimateRepository.getEstimateById((int)chatRoomId)).thenReturn(estimate);
		when(chatRoomRepository.findFirstByEstimate(estimate)).thenReturn(Optional.empty());
		
		// When
		var result = chatService.getChatMemory(chatRoomId);
		
		// Then
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void deleteChat_success(){
		// Given
		int estimateId = 1;
		
		// 모킹
		Estimate estimate = mock(Estimate.class);
		ChatRoom chatRoom = mock(ChatRoom.class);
		
		when(estimateRepository.findById(estimateId)).thenReturn(Optional.of(estimate));
		when(chatRoomRepository.findFirstByEstimate(estimate)).thenReturn(Optional.of(chatRoom));
		
		// When
		chatService.deleteChat(estimateId);
		
		// Then
		verify(chatRepository).deleteByChatRoom(chatRoom);
	}

	@Test
	public void deleteChat_chatRoomNotFound(){
		// Given
		int estimateId = 1;

		// 모킹
		Estimate estimate = mock(Estimate.class);

		when(estimateRepository.findById(estimateId)).thenReturn(Optional.of(estimate));
		when(chatRoomRepository.findFirstByEstimate(estimate)).thenReturn(Optional.empty());

		// When & Then
		assertThrows(NoSuchElementException.class, () -> {
			chatService.deleteChat(estimateId);
		});
	}
	
	@Test
	public void deleteChat_estimateNotFound(){
		// Given
		int estimateId = 1;
		
		// 모킹
		when(estimateRepository.findById(estimateId)).thenReturn(Optional.empty());
		
		// When & Then
		assertThrows(NoSuchElementException.class, () -> {
			chatService.deleteChat(estimateId);
		});
	}
}
