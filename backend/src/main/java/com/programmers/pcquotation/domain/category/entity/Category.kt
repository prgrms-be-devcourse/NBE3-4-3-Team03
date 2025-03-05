package com.programmers.pcquotation.domain.category.entity;

import com.programmers.pcquotation.domain.item.entity.Item;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;


@Entity
class Category(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Long = 0,

	var category: String,

	@OneToMany(mappedBy = "category", cascade = [CascadeType.ALL], orphanRemoval = true)
	val items: MutableList<Item> = mutableListOf()
) {

	protected constructor() : this(0, "")

	fun updateCategory(category: String) {
		this.category = category
	}

	companion object {
		fun createTestCategory(id: Long, categoryName: String): Category {
			return Category(id = id, category = categoryName)
		}
	}
}