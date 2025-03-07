package com.programmers.pcquotation.domain.seller.dto

import com.programmers.pcquotation.domain.seller.entitiy.Seller
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

class SellerSignupRequest(
    @NotNull
    @Size(message = "아이디는 20글자 이하입니다.", max = 20)
    var username: String,

    @NotNull
    @Size(message = "비밀번호는 20글자 이하입니다.", max = 20)
    var password: String,

    @NotNull
    var confirmPassword: String,

    @NotNull
    @Size(message = "회사명은 20글자 이하입니다.", max = 20)
    var companyName: String,

    @NotNull
    @Size(message = "이메일은 100글자 이하입니다.", max = 100)
    var email: String,
    var isVerified: Boolean = false,
    @NotNull
    @Size(message = "본인확인질문은 100글자 이하입니다.", max = 100)
    var verificationQuestion: String,
    @NotNull
    @Size(message = "본인확인대답은 100글자 이하입니다.", max = 100)
    var verificationAnswer: String

) {

    fun toSeller(): Seller {
        return Seller(
            username,
            password,
            companyName,
            email,
            verificationQuestion,
            verificationAnswer
        )
    }
}