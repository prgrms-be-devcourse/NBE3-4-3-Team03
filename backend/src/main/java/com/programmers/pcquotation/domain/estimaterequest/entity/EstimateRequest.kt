package com.programmers.pcquotation.domain.estimaterequest.entity

import com.programmers.pcquotation.domain.customer.entity.Customer
import com.programmers.pcquotation.domain.estimate.entity.Estimate
import com.programmers.pcquotation.domain.estimaterequest.dto.EstimateRequestData
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime


@Entity
class EstimateRequest(
    @Column(length = 20)
    var purpose: String,

    @Column(columnDefinition = "INTEGER")
    var budget: Int,

    @Column(length = 200)
    var otherRequest: String,

    @ManyToOne
    var customer: Customer,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0

    @CreatedDate
    var createDate: LocalDateTime = LocalDateTime.now()

    @Enumerated(EnumType.STRING)
    var status: EstimateRequestStatus = EstimateRequestStatus.Wait // 0: 대기 중, 1: 채택됨

    @OneToMany(mappedBy = "estimateRequest", cascade = [CascadeType.REMOVE])
    var estimate: MutableList<Estimate>? = null

    fun updateEstimateRequest(estimateRequestData: EstimateRequestData) {
        this.purpose = estimateRequestData.purpose
        this.budget = estimateRequestData.budget
        this.otherRequest = estimateRequestData.otherRequest
    }

    fun updateDeliveryStatus(estimateRequestStatus: EstimateRequestStatus) {
        this.status = estimateRequestStatus
    }

    constructor(estimateRequestData: EstimateRequestData, customer: Customer) : this(
        purpose = estimateRequestData.purpose,
        budget = estimateRequestData.budget,
        otherRequest = estimateRequestData.otherRequest,
        customer = customer
    )


    constructor() : this(
        purpose = "",
        budget = 0,
        otherRequest = "",
        customer = Customer()
    )
}
