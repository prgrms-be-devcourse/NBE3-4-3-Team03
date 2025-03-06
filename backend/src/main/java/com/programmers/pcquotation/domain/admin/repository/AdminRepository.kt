package com.programmers.pcquotation.domain.admin.repository

import com.programmers.pcquotation.domain.admin.entitiy.Admin
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AdminRepository : JpaRepository<Admin, Long> {
    fun findByUsername(username: String): Optional<Admin>
    fun findByApiKey(apiKey: String): Optional<Admin>
}