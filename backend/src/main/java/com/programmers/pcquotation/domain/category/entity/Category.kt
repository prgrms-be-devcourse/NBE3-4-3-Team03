package com.programmers.pcquotation.domain.category.entity;

import com.programmers.pcquotation.domain.item.entity.Item;
import jakarta.persistence.*

import jakarta.validation.constraints.NotEmpty

@Entity
class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:NotEmpty
    var category: String,

    @OneToMany(mappedBy = "category", cascade = [CascadeType.ALL], orphanRemoval = true)
    val items: MutableList<Item> = mutableListOf()
) {
    fun updateCategory(category: String) {
        this.category = category
    }
}