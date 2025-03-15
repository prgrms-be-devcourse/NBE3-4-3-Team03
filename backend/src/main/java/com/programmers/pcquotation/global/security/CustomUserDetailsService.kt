package com.programmers.pcquotation.global.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.programmers.pcquotation.domain.admin.service.AdminService;
import com.programmers.pcquotation.domain.customer.service.CustomerService;
import com.programmers.pcquotation.domain.member.entity.Member;
import com.programmers.pcquotation.domain.member.exception.UserNotFoundException
import com.programmers.pcquotation.domain.seller.service.SellerService;
import com.programmers.pcquotation.global.enums.UserType;

@Service
class CustomUserDetailsService(
    private val customerService: CustomerService,
    private val sellerService: SellerService,
    private val adminService: AdminService
) : UserDetailsService {

    fun loadUserByUsername(username: String, userType: UserType): Member {
        return when (userType) {
            UserType.CUSTOMER -> customerService.findCustomerByUsername(username)
                ?: throw UserNotFoundException(username)

            UserType.SELLER -> sellerService.findByUserName(username)
                ?: throw UserNotFoundException(username)

            UserType.ADMIN -> adminService.findAdminByUsername(username)
                ?: throw UserNotFoundException(username)

            else -> throw IllegalArgumentException("잘못된 UserType입니다.")
        }
    }

    override fun loadUserByUsername(username: String): UserDetails {
        throw UnsupportedOperationException("UserType이 필요합니다.")
    }
}