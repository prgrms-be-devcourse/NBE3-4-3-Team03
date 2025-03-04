package com.programmers.pcquotation.domain.seller.controller;

import java.security.Principal;
import java.util.NoSuchElementException;

import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.programmers.pcquotation.domain.member.entitiy.Member;
import com.programmers.pcquotation.domain.seller.dto.SellerInfoRespnse;
import com.programmers.pcquotation.domain.seller.dto.SellerSignupRequest;
import com.programmers.pcquotation.domain.seller.dto.SellerUpdateDto;
import com.programmers.pcquotation.domain.seller.entitiy.Seller;
import com.programmers.pcquotation.domain.seller.service.BusinessConfirmationService;
import com.programmers.pcquotation.domain.seller.service.SellerService;
import com.programmers.pcquotation.global.enums.UserType;
import com.programmers.pcquotation.global.rq.Rq;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seller")
public class SellerController {
	private final SellerService sellerService;
	private final BusinessConfirmationService businessConfirmationService;

	@GetMapping
	@Transactional(readOnly = true)
	public SellerInfoRespnse info(Principal principal) {
		Seller seller = sellerService
				.findByUserName(principal.getName())
				.orElseThrow(() -> new NullPointerException("존재하지 않는 사용자입니다."));

		return SellerInfoRespnse.builder()
			.id(seller.getId())
			.username(seller.getUsername())
			.companyName(seller.getCompanyName())
			.email(seller.getEmail()).build();
	}

	@PutMapping
	public String modify(@RequestBody @Valid SellerUpdateDto customerUpdateDto, Principal principal) {
		Seller seller = sellerService
				.findByUserName(principal.getName())
				.orElseThrow(() -> new NullPointerException("존재하지 않는 사용자입니다."));

		sellerService.modify(seller, customerUpdateDto);
		return "정보수정이 성공했습니다.";
	}

	@GetMapping("/business/{code}/check")
	@Transactional(readOnly = true)
	public String checkCode(@PathVariable("code") String code) {
		if (businessConfirmationService.checkCode(code)) {
			return "true";
		}
		return "false";
	}

}