package com.programmers.pcquotation.domain.item.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.programmers.pcquotation.domain.admin.entitiy.Admin;
import com.programmers.pcquotation.domain.item.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
	@Query("SELECT i FROM Item i JOIN FETCH i.category WHERE i.category.id = :categoryId")
	List<Item> findByCategoryId(Long categoryId);
	Optional<Item> findByName(String name);
}