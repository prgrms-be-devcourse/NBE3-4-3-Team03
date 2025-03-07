package com.programmers.pcquotation.global.security;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.programmers.pcquotation.domain.customer.entity.Customer;
import com.programmers.pcquotation.domain.member.entitiy.Member;
import com.programmers.pcquotation.domain.member.service.AuthService;
import com.programmers.pcquotation.domain.seller.entitiy.Seller;
import com.programmers.pcquotation.domain.seller.service.SellerService;
import com.programmers.pcquotation.global.enums.UserType;
import com.programmers.pcquotation.global.rq.Rq;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {
	private final AuthService authService;
	private final Rq rq;

	record AuthTokens(
		String apiKey,
		String accessToken,
		UserType userType
	) {
	}

	private AuthTokens getAuthTokensFromRequest() {
		String authorization = rq.getHeader("Authorization");

		if (authorization != null && authorization.startsWith("Bearer ")) {
			String token = authorization.substring("Bearer ".length());
			String[] tokenBits = token.split(" ", 3);
			if (tokenBits.length == 3)
				return new AuthTokens(tokenBits[0], tokenBits[1], UserType.fromString(tokenBits[2]));
		}

		String apiKey = rq.getCookieValue("apiKey");
		String accessToken = rq.getCookieValue("accessToken");
		UserType userType = UserType.fromString(rq.getCookieValue("userType"));

		if (apiKey != null && accessToken != null && userType != UserType.NOTHING)
			return new AuthTokens(apiKey, accessToken, userType);

		return null;
	}

	private void refreshAccessToken(Member member, UserType userType) {
		String newAccessToken = authService.getAccessToken(member);

		rq.setHeader("Authorization", "Bearer " + member.getApiKey() + " " + newAccessToken + " " + userType.toString());
		rq.setCookie("accessToken", newAccessToken);
		rq.setCookie("userType", userType.toString());

	}

	private Member refreshAccessTokenByApiKey(String apiKey, UserType userType) {
		Optional<Member> opMemberByApiKey = authService.findByApiKey(apiKey, userType);

		if (opMemberByApiKey.isEmpty()) {
			return null;
		}
		Member member = opMemberByApiKey.get();
		refreshAccessToken(member,userType);

		return member;

	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws
		ServletException,
		IOException {

		AuthTokens authTokens = getAuthTokensFromRequest();

		if (authTokens == null) {
			filterChain.doFilter(request, response);
			return;
		}

		String apiKey = authTokens.apiKey;
		String accessToken = authTokens.accessToken;
		UserType userType = authTokens.userType;

		Member member = authService.getMemberFromAccessToken(accessToken, userType);
		if (member == null)
			member = refreshAccessTokenByApiKey(apiKey, userType);

		if (member != null)
			rq.setLogin(member);

		filterChain.doFilter(request, response);
	}

}
