package com.programmers.pcquotation.domain.alarm

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
class AlarmEntity @JvmOverloads constructor(
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Int = 0,
	val receiverName: String,
	val message: String,
	var isRead: Boolean = false,
	val createdAt: LocalDateTime = LocalDateTime.now()
)