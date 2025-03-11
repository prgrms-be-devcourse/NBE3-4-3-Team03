package com.programmers.pcquotation.domain.estimaterequest.service

import com.programmers.pcquotation.domain.customer.entity.Customer
import com.programmers.pcquotation.domain.estimaterequest.dto.EstimateRequestData
import com.programmers.pcquotation.domain.estimaterequest.dto.EstimateRequestResDto
import com.programmers.pcquotation.domain.estimaterequest.entity.EstimateRequest
import java.util.*

interface EstimateRequestService {
    fun createEstimateRequest(estimateRequestData : EstimateRequestData, customer : Customer)

    fun getEstimateRequestById(id: Int): Optional<EstimateRequest>

    fun getEstimateRequestByCustomerId(customer: Customer): List<EstimateRequestResDto>

    fun findCustomer(name: String): Customer

    fun modify(id: Int, estimateRequestData: EstimateRequestData)

    fun deleteByEstimateId(id: Int)

    fun getAllEstimateRequest():List<EstimateRequestResDto>
}