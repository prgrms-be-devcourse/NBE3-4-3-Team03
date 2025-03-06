package com.programmers.pcquotation.domain.customer.service

import com.programmers.pcquotation.domain.customer.entity.Customer
import com.programmers.pcquotation.domain.customer.repository.CustomerRepository
import com.programmers.pcquotation.domain.member.entitiy.Member
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import java.util.*

@RequiredArgsConstructor
@Service
class CustomerService (
    private val customerRepository: CustomerRepository
){


    fun createCustomer(customer: Customer) {
        customerRepository.save(customer)
    }

    fun findCustomerByUsername(username: String?): Optional<Customer> {
        username ?: return Optional.empty()
        return customerRepository.getCustomerByUsername(username)
    }

    fun findCustomerByEmail(email: String?): Optional<Customer> {
        email ?: return Optional.empty()
        return customerRepository.getCustomerByEmail(email)
    }

    fun findByApiKey(apiKey: String): Optional<Member<*>> {
        return customerRepository.findByApiKey(apiKey).map { customer: Member<*>? -> customer }
    }

    fun findById(id: Long): Optional<Member<*>> {
        return customerRepository.findById(id).map { customer: Member<*>? -> customer }
    }
}