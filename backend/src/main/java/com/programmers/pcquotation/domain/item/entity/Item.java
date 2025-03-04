package com.programmers.pcquotation.domain.item.entity;

import java.util.List;

import com.programmers.pcquotation.domain.category.entity.Category;
import com.programmers.pcquotation.domain.estimate.entity.EstimateComponent;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotEmpty
	private String name;        // 부품 이름
	@NotEmpty
	private String imgFilename;

	@ManyToOne
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	@OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<EstimateComponent> estimateComponents;

	public void updateItem(String name, String imgFilename, Category category) {
		this.name = name;
		this.imgFilename = imgFilename;
		this.category = category;
	}

	public static Item createTestItem(Long id, String name, String imgFilename, Category category) {

		Item item = new Item();
		item.id = id;
		item.name = name;
		item.imgFilename = imgFilename;
		item.category = category;
		return item;
	}
}

