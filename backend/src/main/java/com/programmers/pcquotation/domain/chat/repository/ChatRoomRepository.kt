package com.programmers.pcquotation.domain.chat.repository

import com.programmers.pcquotation.domain.chat.entity.ChatRoom
import com.programmers.pcquotation.domain.estimate.entity.Estimate
import org.springframework.data.jpa.repository.JpaRepository

interface ChatRoomRepository : JpaRepository<ChatRoom, Long> {
    fun deleteByEstimate(estimate: Estimate)

    fun findFirstByEstimate(estimate: Estimate?): ChatRoom?
}