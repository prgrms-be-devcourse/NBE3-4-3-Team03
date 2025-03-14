package com.programmers.pcquotation.domain.item.dto

data class ItemCreateResponse(
    val id: Long,

    val message: String // 예: "부품이 성공적으로 생성되었습니다."
)