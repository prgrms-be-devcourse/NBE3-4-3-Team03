package com.programmers.pcquotation.domain.alarm

import org.springframework.data.jpa.repository.JpaRepository

interface AlarmRepository : JpaRepository<AlarmEntity, Int> {
	fun findByReceiverNameAndIsReadOrderByIdDesc(receiverName: String, isRead: Boolean): List<AlarmEntity>
}