package com.programmers.pcquotation.domain.chat.dto

import java.time.LocalDateTime

data class ChatMemoryRes(
    val sender: String,
    val content: String,
    val sendDate: LocalDateTime,
)