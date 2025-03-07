package com.programmers.pcquotation.domain.customer.entity

import com.programmers.pcquotation.domain.comment.emtity.Comment
import com.programmers.pcquotation.domain.customer.dto.CustomerSignupRequest
import com.programmers.pcquotation.domain.member.entitiy.Member
import com.programmers.pcquotation.domain.seller.dto.SellerSignupRequest
import jakarta.persistence.*
import lombok.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

@Entity
class Customer : Member{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Long? = null

    @Column(unique = true)
    override var username: String? = null
    override var password: String? = null
    var customerName: String = ""

    @Column(unique = true)
    var email: String? = null
    var verificationQuestion: String? = null
    var verificationAnswer: String? = null

    @Column(unique = true)
    override var apiKey: String? = null

    override var authorities: Collection<GrantedAuthority>? =
        listOf("ROLE_CUSTOMER").stream().map { role: String? -> SimpleGrantedAuthority(role) }.toList()

    @OneToMany(mappedBy = "customer", cascade = [CascadeType.ALL], orphanRemoval = true)
    var comments: MutableList<Comment> = mutableListOf()

    constructor(
        username: String, password: String
    ) {
        this.username = username
        this.password = password
    }
    constructor(customerSignupRequest: CustomerSignupRequest, apiKey: String, encodingPassword: String) {
        this.username =  customerSignupRequest.username
        this.password = encodingPassword
        this.customerName = customerSignupRequest.customerName
        this.email = customerSignupRequest.email
        this.verificationQuestion = customerSignupRequest.verificationQuestion
        this.verificationAnswer = customerSignupRequest.verificationAnswer
        this.apiKey = apiKey
    }

    constructor(
        username: String?,
        password: String?,
        customerName: String,
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

    constructor(
        id:Long?,
        username: String?,
        password: String?,
        customerName: String,
        email: String?,
        verificationQuestion: String?,
        verificationAnswer: String?,
        apiKey: String?
    ) {
        this.id = id
        this.username = username
        this.password = password
        this.customerName = customerName
        this.email = email
        this.verificationQuestion = verificationQuestion
        this.verificationAnswer = verificationAnswer
        this.apiKey = apiKey

    }

}