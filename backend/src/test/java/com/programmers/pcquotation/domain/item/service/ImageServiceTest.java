package com.programmers.pcquotation.domain.item.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

	private ImageService imageService;

	@Mock
	private MultipartFile multipartFile;

	@BeforeEach
	void setUp() {
		imageService = new ImageService("src/main/resources/static/image/item/");
	}

	@Nested
	public class ImageStoreTest {

		@Test
		@DisplayName("정상적인 저장")
		void t1() throws IOException {

			String originFilename = "test.png";
			when(multipartFile.isEmpty()).thenReturn(false);
			when(multipartFile.getOriginalFilename()).thenReturn(originFilename);

			String filename = imageService.storeImage(multipartFile);

			assertNotNull(filename);
			verify(multipartFile).transferTo(any(File.class));
		}
	}
}