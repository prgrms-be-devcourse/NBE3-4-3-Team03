package com.programmers.pcquotation.global.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.programmers.pcquotation.domain.admin.service.AdminService;
import com.programmers.pcquotation.domain.customer.service.CustomerService;
import com.programmers.pcquotation.domain.member.entitiy.Member;
import com.programmers.pcquotation.domain.seller.service.SellerService;
import com.programmers.pcquotation.global.enums.UserType;


@Service
class CustomUserDetailsService(
	private val customerService: CustomerService,
	private val sellerService: SellerService,
	private val adminService: AdminService
) : UserDetailsService {

	fun loadUserByUsername(username: String, userType: UserType): Member<*> {
		return when (userType) {
			UserType.CUSTOMER -> customerService.findCustomerByUsername(username)
				.orElseThrow { throwUserNotFoundException(username) }
			UserType.SELLER -> sellerService.findByUserName(username)
				.orElseThrow { throwUserNotFoundException(username) }
			UserType.ADMIN -> adminService.findAdminByUsername(username)
				.orElseThrow { throwUserNotFoundException(username) }
			else -> throw IllegalArgumentException("잘못된 UserType입니다.")
		}
	}

	private fun throwUserNotFoundException(username: String): UsernameNotFoundException {
		return UsernameNotFoundException("해당 유저가 존재하지 않습니다. username = $username")
	}

	override fun loadUserByUsername(username: String): UserDetails {
		throw UnsupportedOperationException("UserType이 필요합니다.")
	}
}