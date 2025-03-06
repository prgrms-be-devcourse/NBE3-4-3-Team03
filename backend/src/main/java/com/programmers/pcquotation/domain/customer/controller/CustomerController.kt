package com.programmers.pcquotation.domain.customer.controller

import com.programmers.pcquotation.domain.customer.dto.CustomerInfoResponse
import com.programmers.pcquotation.domain.customer.service.CustomerService
import lombok.RequiredArgsConstructor
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RequiredArgsConstructor
@RestController
@RequestMapping(("/customer"))
class CustomerController (
    private val customerService: CustomerService
){

    @GetMapping
    @Transactional(readOnly = true)
    fun info(principal: Principal): CustomerInfoResponse {
        val customer = customerService
            .findCustomerByUsername(principal.name)
            .orElseThrow { NullPointerException("존재하지 않는 사용자입니다.") }

        return CustomerInfoResponse(
            customer.id,
            customer.username,
            customer.customerName,
            customer.email)
    }
}