package com.programmers.pcquotation.domain.customer.dto

import com.programmers.pcquotation.domain.customer.entity.Customer


data class CustomerSignupResponse(val customer: Customer, var message: String) {
    val id = customer.id
    val username = customer.username
    val customerName = customer.customerName
    val email = customer.email
}