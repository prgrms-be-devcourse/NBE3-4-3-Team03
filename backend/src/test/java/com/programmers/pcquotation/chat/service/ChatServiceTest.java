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

	// @Test
	// public void getChat_success(){
	// 	// Given
	// 	String username = "테스트 username";
	// 	String content = "테스트용 메세지 입니다.";
	// 	long chatRoomId = 1L;
	//
	// 	// 모킹
	// 	when(estimateRepository.getEstimateById((int)chatRoomId)).thenReturn(null);
	//
	// 	// When & Then
	// 	assertThrows(NoSuchElementException.class, () -> {
	// 		chatService.saveChat(username, content, chatRoomId);
	// 	});
	// }
}
