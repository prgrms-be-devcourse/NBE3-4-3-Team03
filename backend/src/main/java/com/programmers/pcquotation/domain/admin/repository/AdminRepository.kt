package com.programmers.pcquotation.domain.admin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.programmers.pcquotation.domain.admin.entitiy.Admin;
import com.programmers.pcquotation.domain.seller.entitiy.Seller;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
	Optional<Admin> findByUsername(String username);
	Optional<Admin> findByApiKey(String apiKey);

}