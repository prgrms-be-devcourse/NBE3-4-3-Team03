package com.programmers.pcquotation.global.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.programmers.pcquotation.domain.admin.service.AdminService;
import com.programmers.pcquotation.domain.customer.service.CustomerService;
import com.programmers.pcquotation.domain.member.entitiy.Member;
import com.programmers.pcquotation.domain.member.service.AuthService;
import com.programmers.pcquotation.domain.seller.service.SellerService;
import com.programmers.pcquotation.global.enums.UserType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	private final CustomerService customerService;
	private final SellerService sellerService;
	private  final AdminService adminService;

	public Member loadUserByUsername(String username, UserType userType) throws UsernameNotFoundException {
		Member member = switch (userType) {
			case CUSTOMER -> customerService.findCustomerByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("해당 유저가 존재하지 않습니다. username = " + username));
			case SELLER -> sellerService.findByUserName(username)
				.orElseThrow(() -> new UsernameNotFoundException("해당 유저가 존재하지 않습니다. username = " + username));
			case ADMIN -> adminService.findAdminByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("해당 유저가 존재하지 않습니다. username = " + username));
			default -> throw new UsernameNotFoundException("잘못된 UserType입니다.");
		};

		return member;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		throw new UnsupportedOperationException("UserType이 필요합니다.");
	}
}