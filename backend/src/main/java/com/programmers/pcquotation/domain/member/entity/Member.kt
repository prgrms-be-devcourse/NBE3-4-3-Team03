package com.programmers.pcquotation.domain.member.entity

import org.springframework.security.core.GrantedAuthority

interface Member {
    var id: Long?
    var username: String?
    var authorities: Collection<GrantedAuthority>?
    var apiKey: String?
    var password: String?
}