package com.programmers.pcquotation.domain.delivery.repository

import com.programmers.pcquotation.domain.delivery.entity.Delivery
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DeliveryRepository : JpaRepository<Delivery, Int>
