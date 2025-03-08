package com.programmers.pcquotation.domain.seller.dto;

import com.programmers.pcquotation.domain.customer.entity.Customer;
import com.programmers.pcquotation.domain.seller.entitiy.Seller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SellerSignupRequest {
	@NotNull
	@Size(message = "아이디는 20글자 이하입니다.", max = 20)
	String username;
	@NotNull
	@Size(message = "비밀번호는 20글자 이하입니다.", max = 20)
	String password;
	@NotNull
	String confirmPassword;
	@NotNull
	@Size(message = "회사명은 20글자 이하입니다.", max = 20)
	String companyName;
	@NotNull
	@Size(message = "이메일은 100글자 이하입니다.", max = 100)
	String email;
	@NotNull
	@Size(message = "본인확인질문은 100글자 이하입니다.", max = 100)
	String verificationQuestion;
	@NotNull
	@Size(message = "본인확인대답은 100글자 이하입니다.", max = 100)
	String verificationAnswer;
    boolean isVerified;

	public Seller toSeller() {
		return Seller.builder()
			.username(username)
			.password(password)
			.companyName(companyName)
			.email(email)
			.verificationQuestion(verificationQuestion)
			.verificationAnswer(verificationAnswer)
			.build();
	}
}