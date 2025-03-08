package com.programmers.pcquotation.global.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import com.programmers.pcquotation.domain.member.entitiy.Member

class CustomUserDetails(
    private val member: Member<*>
) : UserDetails {

    val id: Long
        get() = member.getId() ?: throw IllegalStateException("Id cannot be null")

    override fun getAuthorities(): Collection<out GrantedAuthority> =
        member.getAuthorities() ?: throw IllegalStateException("Authorities cannot be null")

    override fun getUsername(): String = member.getUsername() ?: throw IllegalStateException("Username cannot be null")

    override fun getPassword(): String = member.getPassword()

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}
