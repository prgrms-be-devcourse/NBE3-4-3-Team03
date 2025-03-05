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

	var totalPrice: Int,

	@OneToMany(mappedBy = "estimate", cascade = [CascadeType.ALL], orphanRemoval = true)
	var estimateComponents: MutableList<EstimateComponent> = mutableListOf(),

	var createDate: LocalDateTime = LocalDateTime.now(),

	@OneToMany(mappedBy = "estimate", cascade = [CascadeType.ALL], orphanRemoval = true)
	var comments: List<Comment> = ArrayList()
) {
	fun addEstimateComponent(component: EstimateComponent) {
		estimateComponents.add(component)
		component.estimate = this
	}
}