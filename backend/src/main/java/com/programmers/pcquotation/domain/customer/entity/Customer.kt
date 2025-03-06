package com.programmers.pcquotation.domain.customer.entity

import com.programmers.pcquotation.domain.comment.emtity.Comment
import com.programmers.pcquotation.domain.member.entitiy.Member
import jakarta.persistence.*
import lombok.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

@Entity
class Customer : Member<Any?> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Long? = null

    @Column(unique = true)
    override var username: String? = null
    override var password: String? = null
    var customerName: String? = null

    @Column(unique = true)
    var email: String? = null
    var verificationQuestion: String? = null
    var verificationAnswer: String? = null

    @Column(unique = true)
    override var apiKey: String? = null

    override var authorities: Collection<GrantedAuthority>? = listOf("ROLE_CUSTOMER").stream()
        .map { role: String? -> SimpleGrantedAuthority(role) }
        .toList()

    @OneToMany(mappedBy = "customer", cascade = [CascadeType.ALL], orphanRemoval = true)
    var comments: MutableList<Comment> = mutableListOf()

    constructor(
        username: String,
        password: String
    ) {
        this.username = username
        this.password =password
    }

    constructor(
        username: String?,
        password: String?,
        customerName: String?,
        email: String?,
        verificationQuestion: String?,
        verificationAnswer: String?
    ) {
        this.username = username
        this.password = password
        this.customerName = customerName
        this.email = email
        this.verificationQuestion = verificationQuestion
        this.verificationAnswer = verificationAnswer
    }

}