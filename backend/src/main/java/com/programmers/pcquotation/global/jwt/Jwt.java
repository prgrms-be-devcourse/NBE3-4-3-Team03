package com.programmers.pcquotation.global.jwt;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class Jwt {
	public static String toString(String secret, long expireSeconds, Map<String, Object> body) {
		Date issuedAt = new Date();
		Date expiration = new Date(issuedAt.getTime() + 1000L * expireSeconds);

		SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());

		String jwt = Jwts.builder()
			.claims(body)
			.issuedAt(issuedAt)
			.expiration(expiration)
			.signWith(secretKey)
			.compact();

		return jwt;
	}

	public static Map<String, Object> payload(String secret, String jwtStr) {
		SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());
		try {
			return (Map<String, Object>) Jwts
				.parser()
				.verifyWith(secretKey)
				.build()
				.parseClaimsJws(jwtStr)
				.getPayload();
		} catch (Exception e) {
			return null;
		}
	}

}
