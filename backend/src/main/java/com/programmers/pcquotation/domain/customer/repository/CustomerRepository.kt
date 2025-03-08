package com.programmers.pcquotation.domain.customer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.programmers.pcquotation.domain.customer.entity.Customer;
import com.programmers.pcquotation.domain.seller.entitiy.Seller;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
	Optional<Customer> getCustomerByUsername(String username);

	Optional<Customer> getCustomerByEmail(String email);

	Optional<Customer> findByApiKey(String apiKey);
}