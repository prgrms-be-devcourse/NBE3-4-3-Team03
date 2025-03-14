package com.programmers.pcquotation.chat.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.programmers.pcquotation.domain.chat.entity.ChatRoom;
import com.programmers.pcquotation.domain.chat.repository.ChatRoomRepository;
import com.programmers.pcquotation.domain.chat.service.ChatRoomService;
import com.programmers.pcquotation.domain.estimate.entity.Estimate;
import com.programmers.pcquotation.domain.estimate.repository.EstimateRepository;

@ActiveProfiles("test")
@SpringBootTest
public class ChatRoomServiceTest {

	@Mock
	private ChatRoomRepository chatRoomRepository;

	@Mock
	private EstimateRepository estimateRepository;

	@InjectMocks
	private ChatRoomService chatRoomService;

	@Test
	public void createChatRoom_success() {

		// Given
		Estimate estimate = mock(Estimate.class);

		// When
		chatRoomService.createChatRoom(estimate);

		// Then
		verify(chatRoomRepository).save(any(ChatRoom.class));
	}

	@Test
	public void createChatRoom_estimateNotFound() {
		// Given
		Estimate estimate = null;

		// When & Then
		assertThrows(NullPointerException.class, () -> {
			chatRoomService.createChatRoom(estimate);
		});
	}

	@Test
	public void deleteChatRoom_success() {
		// Given
		int estimateId = 1;
		Estimate estimate = mock(Estimate.class);

		when(estimateRepository.findById(estimateId)).thenReturn(Optional.of(estimate));

		// When
		chatRoomService.deleteChatRoom(estimateId);

		// Then
		verify(chatRoomRepository).deleteByEstimate(estimate);
	}

	@Test
	public void deleteChatRoom_estimateNotFound() {
		// Given
		int estimateId = 1;

		// 모킹
		when(estimateRepository.findById(estimateId)).thenReturn(Optional.empty());

		// When & Then
		assertThrows(NoSuchElementException.class, () -> {
			chatRoomService.deleteChatRoom(estimateId);
		});
	}
}
