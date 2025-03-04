package com.programmers.pcquotation.domain.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.programmers.pcquotation.domain.category.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
