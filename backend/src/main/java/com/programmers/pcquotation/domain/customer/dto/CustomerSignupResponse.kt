package com.programmers.pcquotation.domain.customer.dto

import com.programmers.pcquotation.domain.customer.entity.Customer
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Getter

@Getter
@AllArgsConstructor
@Builder
class CustomerSignupResponse(customer: Customer, var message: String) {
    val id = customer.id
    val username = customer.username
    val customerName = customer.customerName
    val email = customer.email
}