package com.programmers.pcquotation.domain.seller.entitiy

import com.programmers.pcquotation.domain.member.entitiy.Member
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

@Entity
class Seller : Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    override var id: Long? = null

    @Column(length = 20, unique = true)
    override var username: String? = null

    @Column(length = 255)
    override var password: String? = null

    @Column(length = 20)
    var companyName: String? = null

    @Column(length = 100, unique = true)
    var email: String? = null

    @Column(length = 100)
    var verificationQuestion: String? = null

    @Column(length = 100)
    var verificationAnswer: String? = null
    var isVerified = false

    @Column(unique = true)
    override var apiKey: String? = null

    /*
	// 추천한 유저 목록
	@ManyToMany(fetch = FetchType.LAZY)
	private Set<Customers> recommend = new HashSet<>();
	*/
    // @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<Comment> comments;
    override var authorities: Collection<GrantedAuthority>? = listOf("ROLE_SELLER").stream()
        .map { role: String? -> SimpleGrantedAuthority(role) }
        .toList()

    constructor(
        username: String,
        password: String,
        companyName: String,
        email: String,
        verificationQuestion: String,
        verificationAnswer: String
    ) {
        this.username = username
        this.password = password
        this.companyName = companyName
        this.email = email
        this.verificationQuestion = verificationQuestion
        this.verificationAnswer = verificationAnswer
    }
    constructor(
        id: Long,
        username: String,
        password: String,
        companyName: String,
        email: String,
        verificationQuestion: String,
        verificationAnswer: String,
        isVerified:Boolean,
        apiKey: String
    ) {
        this.id = id
        this.username = username
        this.password = password
        this.companyName = companyName
        this.email = email
        this.verificationQuestion = verificationQuestion
        this.verificationAnswer = verificationAnswer
        this.isVerified = isVerified
        this.apiKey = apiKey
    }

}