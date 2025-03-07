package com.programmers.pcquotation.domain.estimate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.programmers.pcquotation.domain.estimate.entity.Estimate;
import com.programmers.pcquotation.domain.seller.entitiy.Seller;

import jakarta.validation.constraints.NotNull;

public interface EstimateRepository extends JpaRepository<Estimate, Integer> {
	List<Estimate> getAllByEstimateRequest_Id(Integer estimateRequestId);

	List<Estimate> getAllBySeller(@NotNull Seller seller);

	Estimate getEstimateById(Integer id);
}