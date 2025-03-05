package com.programmers.pcquotation.domain.estimaterequest.repository

import com.programmers.pcquotation.domain.customer.entity.Customer
import com.programmers.pcquotation.domain.estimaterequest.entity.EstimateRequest
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface EstimateRequestRepository : JpaRepository<EstimateRequest, Int> {
    fun getEstimateRequestById(id: Int): Optional<EstimateRequest>

    fun getAllByCustomer(customer: Customer): List<EstimateRequest>
}
