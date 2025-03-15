package com.programmers.pcquotation.domain.customer.service

import com.programmers.pcquotation.domain.customer.entity.Customer
import com.programmers.pcquotation.domain.customer.repository.CustomerRepository
import com.programmers.pcquotation.domain.member.entity.Member
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class CustomerService(
    private val customerRepository: CustomerRepository
) {
    fun createCustomer(customer: Customer) {
        customerRepository.save(customer)
    }

    fun findCustomerByUsername(username: String): Customer? {
        return customerRepository.getCustomerByUsername(username)
    }

    fun findCustomerByEmail(email: String): Customer? {
        return customerRepository.getCustomerByEmail(email)
    }

    fun findByApiKey(apiKey: String): Member {
        return customerRepository.findByApiKey(apiKey)
            ?: throw NoSuchElementException()
    }

    fun findById(id: Long): Member {
        return customerRepository.findByIdOrNull(id)
            ?: throw NoSuchElementException()
    }
}