package com.programmers.pcquotation.domain.estimaterequest.service

import com.programmers.pcquotation.domain.customer.entity.Customer
import com.programmers.pcquotation.domain.customer.repository.CustomerRepository
import com.programmers.pcquotation.domain.estimaterequest.dto.EstimateRequestData
import com.programmers.pcquotation.domain.estimaterequest.dto.EstimateRequestResDto
import com.programmers.pcquotation.domain.estimaterequest.entity.EstimateRequest
import com.programmers.pcquotation.domain.estimaterequest.exception.NullEntityException
import com.programmers.pcquotation.domain.estimaterequest.repository.EstimateRequestRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
open class EstimateRequestServiceByJpa (
    private val estimateRequestRepository: EstimateRequestRepository,
    private val customerRepository: CustomerRepository
) : EstimateRequestService{

    override fun createEstimateRequest(estimateRequestData :EstimateRequestData, customer :Customer) {
        val estimateRequest = EstimateRequest(estimateRequestData, customer)
        estimateRequestRepository.save(estimateRequest)
    }

    override fun getEstimateRequestById(id: Int): EstimateRequest {
        return estimateRequestRepository.getEstimateRequestById(id).orElseThrow{NullEntityException()}
    }

    override fun findCustomer(name: String): Customer {
        return customerRepository.getCustomerByUsername(name)
            .orElseThrow { NoSuchElementException("고객을 찾을수 없습니다.") }
    }

    override fun modify(id: Int, estimateRequestData: EstimateRequestData) {
        val estimateRequest = estimateRequestRepository
            .findById(id)
            .orElseThrow { NullEntityException() }
        estimateRequest.updateEstimateRequest(estimateRequestData)
    }

    override fun deleteByEstimateId(id: Int) {
        val estimateRequest = estimateRequestRepository
            .findById(id)
            .orElseThrow { NullEntityException() }
        estimateRequestRepository.delete(estimateRequest)
    }

    override fun getAllEstimateRequest():List<EstimateRequestResDto> {
        val allEstimateRequest = estimateRequestRepository.findAll()
        return allEstimateRequest.map { estimateRequest ->
            EstimateRequestResDto(estimateRequest)
        }
    }

    override fun getEstimateRequestByCustomerId(customer: Customer): List<EstimateRequestResDto> {
        val allByCustomer = estimateRequestRepository.getAllByCustomer(customer)
        return allByCustomer.map { request: EstimateRequest -> EstimateRequestResDto(request) }
    }
}
