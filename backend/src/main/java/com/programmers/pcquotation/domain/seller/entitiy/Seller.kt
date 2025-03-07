package com.programmers.pcquotation.domain.seller.entitiy

import com.programmers.pcquotation.domain.member.entitiy.Member
import com.programmers.pcquotation.domain.seller.dto.SellerSignupRequest
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

@Entity
class Seller(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    override var id: Long? = null,

    @Column(length = 20, unique = true)
    override var username: String? = null,

    @Column(length = 255)
    override var password: String? = null,

    @Column(length = 20)
    var companyName: String = "",

    @Column(length = 100, unique = true)
    var email: String? = null,

    @Column(length = 100)
    var verificationQuestion: String? = null,

    @Column(length = 100)
    var verificationAnswer: String? = null,
    var isVerified:Boolean = false,

    @Column(unique = true)
    override var apiKey: String? = null

) : Member {

    override var authorities: Collection<GrantedAuthority>? = listOf("ROLE_SELLER").stream()
        .map { role: String? -> SimpleGrantedAuthority(role) }
        .toList()

    constructor(sellerSignupRequest: SellerSignupRequest,apiKey: String,encodingPassword: String) : this() {
        this.username =  sellerSignupRequest.username
        this.password = encodingPassword
        this.companyName = sellerSignupRequest.companyName
        this.email = sellerSignupRequest.email
        this.verificationQuestion = sellerSignupRequest.verificationQuestion
        this.verificationAnswer = sellerSignupRequest.verificationAnswer
        this.apiKey = apiKey
    }


    constructor(
        username: String,
        password: String,
        companyName: String,
        email: String,
        verificationQuestion: String,
        verificationAnswer: String
    ) : this() {
        this.username = username
        this.password = password
        this.companyName = companyName
        this.email = email
        this.verificationQuestion = verificationQuestion
        this.verificationAnswer = verificationAnswer
    }


}