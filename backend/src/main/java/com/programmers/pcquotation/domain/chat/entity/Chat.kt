package com.programmers.pcquotation.domain.chat.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime


@Entity
class Chat(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    val chatRoom: ChatRoom?,

    @Column(nullable = false)
    var sender: String,  // 보낸 사람

    @Column(columnDefinition = "TEXT", nullable = false)
    var message: String  // 메시지 내용
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @CreationTimestamp
    @Column(updatable = false)
    val sendDate: LocalDateTime = LocalDateTime.now()

}

