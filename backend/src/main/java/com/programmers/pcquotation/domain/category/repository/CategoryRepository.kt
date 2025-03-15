package com.programmers.pcquotation.domain.category.repository

import com.programmers.pcquotation.domain.category.entity.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CategoryRepository : JpaRepository<Category, Long> {
    fun findByCategory(category: String): Category?
}