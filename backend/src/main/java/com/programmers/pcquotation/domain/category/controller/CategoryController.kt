package com.programmers.pcquotation.domain.category.controller

import com.programmers.pcquotation.domain.category.dto.*
import com.programmers.pcquotation.domain.category.service.CategoryService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin/categories")
class CategoryController(
    private val categoryService: CategoryService
) {
    // 카테고리 추가
    @PostMapping
    fun createCategory(@RequestBody request: CategoryCreateRequest): CategoryCreateResponse {
        return categoryService.addCategory(request)
    }

    @GetMapping
    fun getCategoryList(): List<CategoryInfoResponse> {
        return categoryService.getCategoryList()
    }

    //카테고리 수정
    @PutMapping("/{id}")
    fun updateCategory(
        @PathVariable id: Long,
        @RequestBody request: CategoryUpdateRequest
    ): CategoryUpdateResponse {
        return categoryService.updateCategory(id, request)
    }

    //카테고리 삭제
    @DeleteMapping("/{id}")
    fun deleteCategory(@PathVariable id: Long): CategoryDeleteResponse {
        return categoryService.deleteCategory(id)
    }
}