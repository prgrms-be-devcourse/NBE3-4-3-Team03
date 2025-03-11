package com.programmers.pcquotation.domain.chat.entity

import com.programmers.pcquotation.domain.estimate.entity.Estimate
import jakarta.persistence.*

@Entity
class ChatRoom(
    @OneToOne(cascade = [CascadeType.DETACH], fetch = FetchType.LAZY)
    @JoinColumn(name = "estimate_id")
    var estimate: Estimate?
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}