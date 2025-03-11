package com.programmers.pcquotation.domain.admin.entitiy

import com.programmers.pcquotation.domain.member.entitiy.Member
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

@Entity
class Admin : Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override // AUTO_INCREMENT
    var id: Long? = null

    @Column(length = 20, unique = true)
    override var username: String? = null

    @Column(length = 255)
    override var password: String? = null

    @Column(unique = true)
    override var apiKey: String? = null

    override var authorities: Collection<GrantedAuthority>? = listOf("ROLE_ADMIN").stream()
        .map { role: String? -> SimpleGrantedAuthority(role) }
        .toList()

    constructor(username: String, password: String, apiKey: String) {
        this.username = username
        this.password = password
        this.apiKey = apiKey
    }
}