package com.programmers.pcquotation.domain.member.entitiy

import org.springframework.security.core.GrantedAuthority

interface Member<T> {
    fun getId():Long?
    fun getUsername():String?
    fun getAuthorities():Collection<GrantedAuthority>?
    fun getApiKey():String
    fun getPassword():String
}