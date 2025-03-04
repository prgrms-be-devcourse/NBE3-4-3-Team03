package com.programmers.pcquotation.global.rq;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import com.programmers.pcquotation.domain.customer.service.CustomerService;
import com.programmers.pcquotation.domain.estimaterequest.exception.NullEntityException;
import com.programmers.pcquotation.domain.member.entitiy.Member;
import com.programmers.pcquotation.domain.member.service.AuthService;
import com.programmers.pcquotation.domain.seller.service.SellerService;
import com.programmers.pcquotation.global.enums.UserType;
import com.programmers.pcquotation.global.security.CustomAuthenticationFilter;
import com.programmers.pcquotation.global.security.CustomUserDetails;
import com.programmers.pcquotation.global.security.CustomUserDetailsService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequestScope
@Component
@RequiredArgsConstructor
public class Rq {
	private final HttpServletRequest req;
	private final HttpServletResponse resp;
	private final AuthService authService;
	private final SellerService sellerService;
	private final CustomUserDetailsService customUserDetailsService;

	public void setLogin(Member member) {
		CustomUserDetails user = new CustomUserDetails(member);
		Authentication authentication = new UsernamePasswordAuthenticationToken(
			user,
			user.getPassword(),
			user.getAuthorities()
		);
		SecurityContextHolder.getContext().setAuthentication(authentication);

	}

	public Member getMember() {
		String authorization = getHeader("Authorization");
		if(authorization.isEmpty()) throw new NullEntityException();
		String token = authorization.substring("Bearer ".length());
		String[] tokenBits = token.split(" ", 3);
		return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
			.map(Authentication::getPrincipal)
			.filter(UserDetails.class::isInstance)
			.map(UserDetails.class::cast)
			.map(userDetails ->
				customUserDetailsService
					.loadUserByUsername(userDetails.getUsername(), UserType.valueOf(tokenBits[2])))
			.orElse(null);
	}

	public void setHeader(String name, String value) {
		resp.setHeader(name, value);
	}

	public String getHeader(String name) {
		return req.getHeader(name);
	}

	public void setCookie(String name, String value) {
		ResponseCookie cookie = ResponseCookie.from(name, value)
			.path("/")
			.domain("localhost")
			.sameSite("Strict")
			.secure(true)
			.httpOnly(true)
			.build();

		resp.addHeader("Set-Cookie", cookie.toString());
	}

	public String getCookieValue(String name) {
		return Optional
			.ofNullable(req.getCookies())
			.stream() // 1 ~ 0
			.flatMap(cookies -> Arrays.stream(cookies))
			.filter(cookie -> cookie.getName().equals(name))
			.map(cookie -> cookie.getValue())
			.findFirst()
			.orElse(null);
	}

	public void deleteCookie(String name) {
		ResponseCookie cookie = ResponseCookie.from(name, null)
			.path("/")
			.domain("localhost")
			.sameSite("Strict")
			.secure(true)
			.httpOnly(true)
			.maxAge(0)
			.build();

		resp.addHeader("Set-Cookie", cookie.toString());
	}
}