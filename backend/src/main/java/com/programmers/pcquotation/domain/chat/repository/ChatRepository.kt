package com.programmers.pcquotation.domain.chat.repository

import com.programmers.pcquotation.domain.chat.entity.Chat
import com.programmers.pcquotation.domain.chat.entity.ChatRoom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


interface ChatRepository : JpaRepository<Chat, Long>{
    fun findByChatRoom(chatRoom: ChatRoom): MutableList<Chat>
}