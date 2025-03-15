package com.programmers.pcquotation.domain.admin.repository

import com.programmers.pcquotation.domain.admin.entity.Admin
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AdminRepository : JpaRepository<Admin, Long> {
    fun findByUsername(username: String): Admin?

    fun findByApiKey(apiKey: String): Admin?
}