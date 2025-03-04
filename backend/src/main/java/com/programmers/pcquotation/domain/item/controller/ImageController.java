package com.programmers.pcquotation.domain.item.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.pcquotation.domain.item.service.ImageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/image")
public class ImageController {

	private final ImageService imageService;

	@GetMapping("/{filename}")
	public Resource getImage(
		@PathVariable String filename) {
		log.info("Fetching image with filename: {}", filename); // 로그 추가

		return imageService.getImageByFilename(filename);
	}

	@GetMapping("/path/{filename}")
	public ResponseEntity<Resource> getImageFileName(@PathVariable String filename) {
		try {
			// 이미지 Resource 가져오기
			Resource resource = imageService.getImageByFilename(filename);
			return ResponseEntity.ok()
				.contentType(MediaType.IMAGE_JPEG) // 또는 적절한 미디어 타입
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
				.body(resource);
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}
}

