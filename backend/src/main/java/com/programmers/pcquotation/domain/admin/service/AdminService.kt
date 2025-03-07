package com.programmers.pcquotation.domain.admin.service

import com.programmers.pcquotation.domain.admin.entitiy.Admin
import com.programmers.pcquotation.domain.admin.repository.AdminRepository
import com.programmers.pcquotation.domain.member.entitiy.Member
import org.springframework.stereotype.Service
import java.util.*

@Service
class AdminService(
    private val adminRepository: AdminRepository
) {

    fun findAdminByUsername(username: String): Optional<Admin> {
        return adminRepository.findByUsername(username)
    }

    fun findById(id: Long): Optional<Member> {
        return adminRepository.findById(id).map { admin: Member -> admin }
    }

    fun create(admin: Admin) {
        adminRepository.save(admin)
    }

    fun findByApiKey(apiKey: String): Optional<Member> {
        return adminRepository.findByApiKey(apiKey).map { admin: Member -> admin }
    }
}
