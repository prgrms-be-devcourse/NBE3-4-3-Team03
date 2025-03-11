package com.programmers.pcquotation.domain.item.controller

import com.programmers.pcquotation.domain.item.dto.*
import com.programmers.pcquotation.domain.item.service.ItemService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin/items")
class ItemController(
    private val itemService: ItemService
) {
    @PostMapping
    fun createItem(@ModelAttribute("request") request: ItemCreateRequest): ItemCreateResponse {
        return itemService.addItem(request)
    }

    @GetMapping(params = ["categoryId"])
    fun getInfoListByCategory(@RequestParam(required = true) categoryId: Long): List<ItemInfoResponse> {
        return itemService.getItemsByCategory(categoryId)
    }

    @GetMapping
    fun getInfoList(): List<ItemInfoResponse> {
        return itemService.getItemList()
    }

    @PutMapping("/{id}")
    fun updateItem(
        @PathVariable id: Long,
        @ModelAttribute("request") request: ItemUpdateRequest
    ): ItemUpdateResponse {
        return itemService.updateItem(id, request)
    }

    @DeleteMapping("/{id}")
    fun deleteItem(@PathVariable id: Long): ItemDeleteResponse {
        return itemService.deleteItem(id)
    }
}