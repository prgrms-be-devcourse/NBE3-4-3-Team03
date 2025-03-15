package com.programmers.pcquotation.domain.item.dto

import jakarta.validation.constraints.NotBlank

data class ItemUpdateRequest(
    @NotBlank
    val name: String,

    @NotBlank
    val imgFilename: String,

    val categoryId: Long
)