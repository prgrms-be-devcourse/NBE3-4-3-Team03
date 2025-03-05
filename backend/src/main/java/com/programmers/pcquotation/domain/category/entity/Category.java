package com.programmers.pcquotation.domain.category.entity;

import java.util.ArrayList;
import java.util.List;

import com.programmers.pcquotation.domain.item.entity.Item;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String category;

	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
	final private List<Item> items = new ArrayList<>();

	public void updateCategory(String category) {
		this.category = category;
	}

	public static Category createTestCategory(
		Long id,
		String categoryName
	) {

		Category category = new Category();
		category.id = id;
		category.category = categoryName;
		return category;
	}
}