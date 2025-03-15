package com.programmers.pcquotation.domain.chat.dto

import java.time.LocalDateTime

data class ChatMessageRes(
    val username: String,
    val content: String,
    val sendDate: LocalDateTime = LocalDateTime.now()
)