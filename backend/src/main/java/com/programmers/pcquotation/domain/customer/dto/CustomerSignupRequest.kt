package com.programmers.pcquotation.domain.customer.dto

import com.programmers.pcquotation.domain.customer.entity.Customer
import lombok.AllArgsConstructor
import lombok.Getter


data class CustomerSignupRequest (
    var username: String? = null,
    var password: String? = null,
    var confirmPassword: String? = null,
    var customerName: String? = null,
    var email: String? = null,
    var verificationQuestion: String? = null,
    var verificationAnswer: String? = null,

)
{

    fun toCustomer(): Customer {
        return Customer(
            username,
            password,
            customerName,
            email,
            verificationQuestion,
            verificationAnswer
        )
    }
}