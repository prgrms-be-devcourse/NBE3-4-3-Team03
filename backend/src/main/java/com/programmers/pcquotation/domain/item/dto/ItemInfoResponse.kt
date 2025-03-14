package com.programmers.pcquotation.domain.item.dto;

import jakarta.validation.constraints.NotBlank;

data class ItemInfoResponse(
    @NotBlank
    val id: Long,

    @NotBlank
    val name: String,

    @NotBlank
    val categoryId: Long,

    @NotBlank
    val categoryName: String,

    @NotBlank
    val filename: String
)