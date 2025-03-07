package com.programmers.pcquotation.domain.item.entity;

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


@Entity
class Item(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @field:NotEmpty
    var name: String,

    @field:NotEmpty
    var imgFilename: String,

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    var category: Category,

    @OneToMany(mappedBy = "item", cascade = [CascadeType.ALL], orphanRemoval = true)
    var estimateComponents: MutableList<EstimateComponent> = mutableListOf()

) {

    fun updateItem(name: String, imgFilename: String, category: Category) {
        this.name = name
        this.imgFilename = imgFilename
        this.category = category
    }
}