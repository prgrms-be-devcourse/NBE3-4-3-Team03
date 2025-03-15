package com.programmers.pcquotation.domain.item.controller

import com.programmers.pcquotation.domain.item.service.ImageService
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/image")
class ImageController(
    private val imageService: ImageService
) {
    @GetMapping("/{filename}")
    fun getImage(
        @PathVariable filename: String
    ): Resource {
        return imageService.getImageByFilename(filename)
    }

    @GetMapping("/path/{filename}")
    fun getImageFileName(@PathVariable filename: String): ResponseEntity<Resource> {
        try {
            // 이미지 Resource 가져오기
            val resource = imageService.getImageByFilename(filename)
            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // 또는 적절한 미디어 타입
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"$filename\"")
                .body(resource)
        } catch (e: Exception) {
            return ResponseEntity.notFound().build()
        }
    }
}