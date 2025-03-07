package com.programmers.pcquotation.domain.customer.dto

import com.programmers.pcquotation.domain.customer.entity.Customer


data class CustomerSignupRequest(
    var username: String = "",
    var password: String = "",
    var confirmPassword: String = "",
    var customerName: String = "",
    var email: String = "",
    var verificationQuestion: String = "",
    var verificationAnswer: String = "",

    ) {

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