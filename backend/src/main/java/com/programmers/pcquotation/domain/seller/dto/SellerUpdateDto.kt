package com.programmers.pcquotation.domain.seller.dto

import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

class SellerUpdateDto(
    @Size(message = "아이디는 20글자 이하입니다.", max = 20)
    var userName: String = "",

    @NotNull
    @Size(message = "비밀번호는 20글자 이하입니다.", max = 20)
    var password: String = "",

    @Size(message = "회사명은 20글자 이하입니다.", max = 20)
    var companyName: String = "",

    @Size(message = "이메일은 100글자 이하입니다.", max = 20)
    var email: String = "",

    @Size(message = "비밀번호는 20글자 이하입니다.", max = 20)
    var newPassword: String = "",

    var confirmNewPassword: String = ""
) {
    val isValid: @AssertTrue(message = "패스워드를 제외한 필드를 하나 이상 입력해야 합니다.") Boolean
        get() = companyName != "" || email != "" || newPassword != ""
}