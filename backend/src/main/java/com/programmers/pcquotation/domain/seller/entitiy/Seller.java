package com.programmers.pcquotation.domain.seller.entitiy;

import static jakarta.persistence.GenerationType.*;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.programmers.pcquotation.domain.member.entitiy.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seller implements Member {
	@Id
	@GeneratedValue(strategy = IDENTITY) // AUTO_INCREMENT
	private Long id;
	@Column(length = 20, unique = true)
	private String username;
	@Column(length = 255)
	private String password;
	@Column(length = 20)
	private String companyName;
	@Column(length = 100, unique = true)
	private String email;
	@Column(length = 100)
	private String verificationQuestion;
	@Column(length = 100)
	private String verificationAnswer;
	private boolean isVerified;
	@Column(unique = true)
	private String apiKey;
	/*
	// 추천한 유저 목록
	@ManyToMany(fetch = FetchType.LAZY)
	private Set<Customers> recommend = new HashSet<>();
	*/

	// @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
	// private List<Comment> comments;

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of("ROLE_SELLER")
			.stream()
			.map(SimpleGrantedAuthority::new)
			.toList();
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getApiKey() {
		return apiKey;
	}

	@Override
	public String getPassword() {
		return password;
	}

}