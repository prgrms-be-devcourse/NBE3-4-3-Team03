package com.programmers.pcquotation.domain.seller.dto;

import com.programmers.pcquotation.domain.customer.entity.Customer;
import com.programmers.pcquotation.domain.seller.entitiy.Seller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SellerSignupResponse {
	private final Long id;
	private final String username;
	private final String companyName;
	private final String email;
	private final String message;

	public SellerSignupResponse(Seller seller,String message) {
		this.id = seller.getId();
		this.username = seller.getUsername();
		this.companyName = seller.getCompanyName();
		this.email = seller.getEmail();
		this.message = message;
	}
}