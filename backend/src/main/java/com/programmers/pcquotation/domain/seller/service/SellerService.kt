package com.programmers.pcquotation.domain.seller.service;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.programmers.pcquotation.domain.member.entitiy.Member;
import com.programmers.pcquotation.domain.seller.dto.SellerSignupRequest;
import com.programmers.pcquotation.domain.seller.dto.SellerUpdateDto;
import com.programmers.pcquotation.domain.seller.entitiy.Seller;
import com.programmers.pcquotation.domain.seller.repository.SellerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SellerService {
	private final SellerRepository sellerRepository;
	private final AuthTokenService authTokenService;
	private final PasswordEncoder passwordEncoder;


	public Optional<Seller> findByUserName(String name) {
		return sellerRepository.findByUsername(name);
	}

	public Seller createSeller(Seller seller) {
		sellerRepository.save(seller);
		return seller;
	}

	public Seller modify(Seller seller, SellerUpdateDto customerUpdateDto) {
		if (seller.getPassword().equals(customerUpdateDto.getPassword())) {
			if (!customerUpdateDto.getUserName().isEmpty())
				seller.setUsername(customerUpdateDto.getUserName());
			if (!customerUpdateDto.getCompanyName().isEmpty())
				seller.setCompanyName(customerUpdateDto.getCompanyName());
			if (!customerUpdateDto.getNewPassword().isEmpty()) {
				if (customerUpdateDto.getNewPassword().equals(customerUpdateDto.getConfirmNewPassword()))
					seller.setPassword(customerUpdateDto.getNewPassword());
				else
					throw new NoSuchElementException("비밀번호가 일치하지않습니다.");
			}
		}
		this.sellerRepository.save(seller);
		return seller;
	}
	public void setIsVerified(Seller seller,boolean isVerified){
		seller.setVerified(isVerified);
		sellerRepository.save(seller);
	}
	public Optional<Member> findById(Long id) {
		return sellerRepository.findById(id).map(seller -> seller);
	}

	public Optional<Member> findByApiKey(String apiKey) {
		return sellerRepository.findByApiKey(apiKey).map(seller-> seller);
	}
	public boolean matchPassword(Seller sellers,String password){
		return passwordEncoder.matches(password,sellers.getPassword());
	}

	public Optional<Seller> findSellerByUsername(String username) {
		return sellerRepository.findByUsername(username);
	}

	public Optional<Seller> findSellerByEmail(String email) {
		return sellerRepository.findByEmail(email);
	}
}