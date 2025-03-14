package com.programmers.pcquotation.domain.estimate.repository

import com.programmers.pcquotation.domain.estimate.entity.Estimate
import com.programmers.pcquotation.domain.seller.entity.Seller
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface EstimateRepository : JpaRepository<Estimate, Int> {
    fun getAllByEstimateRequestId(estimateRequestId: Int): List<Estimate>

    fun getAllBySeller(seller: Seller): List<Estimate>

    fun findAllBySeller(seller: Seller, pageable: Pageable): Page<Estimate>

    fun getEstimateById(id: Int): Estimate
}