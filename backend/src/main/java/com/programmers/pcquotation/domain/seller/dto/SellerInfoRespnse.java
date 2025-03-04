package com.programmers.pcquotation.domain.seller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//임시구현
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SellerInfoRespnse {
	private Long id;
	private String username;
	private String companyName;
	private String email;
}
