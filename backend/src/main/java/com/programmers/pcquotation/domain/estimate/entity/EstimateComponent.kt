package com.programmers.pcquotation.domain.estimate.entity

import com.programmers.pcquotation.domain.item.entity.Item
import jakarta.persistence.*

@Entity
class EstimateComponent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @JoinColumn(name = "item_id")
    @ManyToOne
    var item: Item,

    var price: Int,

    @JoinColumn(name = "estimate_id")
    @ManyToOne
    var estimate: Estimate
)