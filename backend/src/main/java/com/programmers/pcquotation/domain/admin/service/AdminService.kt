package com.programmers.pcquotation.domain.admin.service

import com.programmers.pcquotation.domain.admin.entity.Admin
import com.programmers.pcquotation.domain.admin.repository.AdminRepository
import com.programmers.pcquotation.domain.member.entity.Member
import com.programmers.pcquotation.domain.member.exception.UserNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class AdminService(
    private val adminRepository: AdminRepository
) {

    fun findAdminByUsername(username: String): Admin? {
        return adminRepository.findByUsername(username)
    }

    fun findById(id: Long): Member {
        return adminRepository.findByIdOrNull(id)
            ?: throw UserNotFoundException("username does not exist")
    }

    fun create(admin: Admin) {
        adminRepository.save(admin)
    }

    fun findByApiKey(apiKey: String): Member {
        return adminRepository.findByApiKey(apiKey)
            ?: throw UserNotFoundException("username does not exist")
    }
}