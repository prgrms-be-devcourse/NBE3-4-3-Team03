package com.programmers.pcquotation.domain.item.dto

import jakarta.validation.constraints.NotEmpty
import org.springframework.web.multipart.MultipartFile

data class ItemCreateRequest(
    val categoryId: Long,
    @NotEmpty
    val name: String,
    val image: MultipartFile
)