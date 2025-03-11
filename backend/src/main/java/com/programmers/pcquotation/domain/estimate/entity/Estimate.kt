package com.programmers.pcquotation.domain.estimate.entity

import com.programmers.pcquotation.domain.comment.emtity.Comment
import com.programmers.pcquotation.domain.estimaterequest.entity.EstimateRequest
import com.programmers.pcquotation.domain.seller.entitiy.Seller
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Estimate(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var id: Int = 0,

	@ManyToOne
	var estimateRequest: EstimateRequest,

	@ManyToOne
	var seller: Seller,

	var totalPrice: Int = 0,

	@OneToMany(mappedBy = "estimate", cascade = [CascadeType.ALL], orphanRemoval = true)
	private val _estimateComponents: MutableList<EstimateComponent> = mutableListOf(),

	var createDate: LocalDateTime = LocalDateTime.now(),

	@OneToMany(mappedBy = "estimate", cascade = [CascadeType.ALL], orphanRemoval = true)
	var comments: List<Comment> = ArrayList(),

	@Column(nullable = false)
	var isAdopted: Boolean = false
) {
	val estimateComponents: List<EstimateComponent>
		get() = _estimateComponents.toList()

	fun addEstimateComponents(components: List<EstimateComponent>) {
		components.forEach { component ->
			addEstimateComponent(component)
		}
	}

	fun deleteEstimateComponents() {
		_estimateComponents.clear()
	}

	private fun addEstimateComponent(component: EstimateComponent) {
		_estimateComponents.add(component)
		component.estimate = this
	}
}