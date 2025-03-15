package com.programmers.pcquotation.domain.item.service

import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException

@ActiveProfiles("test")
@ExtendWith(MockitoExtension::class)
class ImageServiceTest {
    private lateinit var imageService: ImageService

    @Mock
    private lateinit var multipartFile: MultipartFile

    @BeforeEach
    fun setUp() {
        imageService = ImageService("src/main/resources/static/image/item/")
    }

    @Test
    @DisplayName("정상적인 저장")
    @Throws(IOException::class)
    fun t1() {
        val originFilename = "test.png"
        Mockito.`when`(multipartFile.isEmpty).thenReturn(false)
        Mockito.`when`(multipartFile.originalFilename).thenReturn(originFilename)

        val filename = imageService.storeImage(multipartFile)

        Assertions.assertNotNull(filename)
        Mockito.verify(multipartFile).transferTo(ArgumentMatchers.any(File::class.java))
    }
}