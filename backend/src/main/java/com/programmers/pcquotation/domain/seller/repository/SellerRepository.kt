package com.programmers.pcquotation.domain.seller.repository

import com.programmers.pcquotation.domain.seller.entity.Seller
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SellerRepository : JpaRepository<Seller, Long> {
    fun findByUsername(username: String): Seller?

    fun findByApiKey(apiKey: String): Seller?

    fun findByEmail(email: String): Seller?
}