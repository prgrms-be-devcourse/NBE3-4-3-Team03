package com.programmers.pcquotation.domain.item.repository

import com.programmers.pcquotation.domain.item.entity.Item
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ItemRepository : JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item i JOIN FETCH i.category WHERE i.category.id = :categoryId")
    fun findByCategoryId(categoryId: Long): List<Item>

    fun findByName(name: String): Item?
}