package com.programmers.pcquotation.domain.seller.dto;

import jakarta.validation.constraints.AssertTrue;
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
public class SellerUpdateDto {
	@Size(message = "아이디는 20글자 이하입니다.", max = 20)
	String userName;
	@NotNull
	@Size(message = "비밀번호는 20글자 이하입니다.", max = 20)
	String password;
	@Size(message = "회사명은 20글자 이하입니다.", max = 20)
	String companyName;
	@Size(message = "이메일은 100글자 이하입니다.", max = 20)
	String email;
	@Size(message = "비밀번호는 20글자 이하입니다.", max = 20)
	String newPassword;
	String confirmNewPassword;

	@AssertTrue(message = "패스워드를 제외한 필드를 하나 이상 입력해야 합니다.")
	public boolean isValid() {
		return companyName != null || email != null || newPassword != null;
	}
}
