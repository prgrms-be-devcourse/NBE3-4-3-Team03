package com.programmers.pcquotation.domain.customer.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.programmers.pcquotation.domain.comment.emtity.Comment;
import com.programmers.pcquotation.domain.member.entitiy.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Customer implements Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	private String username;
	private String password;
	private String customerName;
	@Column(unique = true)
	private String email;
	private String verificationQuestion;
	private String verificationAnswer;
	@Column(unique = true)
	private String apiKey;

	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comment> comments;

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of("ROLE_CUSTOMER")
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