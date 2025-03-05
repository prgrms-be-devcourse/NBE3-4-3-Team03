package com.programmers.pcquotation.domain.delivery.repository;

import com.programmers.pcquotation.domain.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {
}
