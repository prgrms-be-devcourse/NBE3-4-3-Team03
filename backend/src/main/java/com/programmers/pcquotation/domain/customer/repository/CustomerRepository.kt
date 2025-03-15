package com.programmers.pcquotation.domain.customer.repository

import com.programmers.pcquotation.domain.customer.entity.Customer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CustomerRepository : JpaRepository<Customer, Long> {
    fun getCustomerByUsername(username: String): Customer?

    fun getCustomerByEmail(email: String): Customer?

    fun findByApiKey(apiKey: String): Customer?
}