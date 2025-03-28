package com.programmers.pcquotation.domain.category.service;

import org.springframework.stereotype.Service;

import com.programmers.pcquotation.domain.category.dto.CategoryCreateRequest;
import com.programmers.pcquotation.domain.category.dto.CategoryCreateResponse;
import com.programmers.pcquotation.domain.category.dto.CategoryDeleteResponse;
import com.programmers.pcquotation.domain.category.dto.CategoryInfoResponse;
import com.programmers.pcquotation.domain.category.dto.CategoryUpdateRequest;
import com.programmers.pcquotation.domain.category.dto.CategoryUpdateResponse;
import com.programmers.pcquotation.domain.category.entity.Category;
import com.programmers.pcquotation.domain.category.exception.CategoryNotFoundException;
import com.programmers.pcquotation.domain.category.repository.CategoryRepository;

import jakarta.transaction.Transactional;
import org.springframework.data.repository.findByIdOrNull

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository
) {

    private fun getCategory(id: Long): Category {
        return categoryRepository.findByIdOrNull(id)
            ?: throw CategoryNotFoundException(id)
    }

    @Transactional
    fun addCategory(request: CategoryCreateRequest): CategoryCreateResponse {
        val category = Category(category = request.category)

        val savedCategory = categoryRepository.save(category)

        return CategoryCreateResponse(savedCategory.id, "카테고리 생성 완료")
    }

    @Transactional
    fun addCategory(categoryName: String): Category {
        val category = categoryRepository.findByCategory((categoryName))

        return if (category == null) {
            val newCategory = Category(category = categoryName)
            categoryRepository.save(newCategory)
        } else {
            category
        }
    }

    fun getCategoryList(): List<CategoryInfoResponse> {
        return categoryRepository.findAll()
            .map { category ->
                CategoryInfoResponse(
                    id = category.id,
                    category = category.category
                )
            }
    }

    @Transactional
    fun updateCategory(id: Long, request: CategoryUpdateRequest): CategoryUpdateResponse {
        val category = getCategory(id)
        category.updateCategory(request.category)

        categoryRepository.save(category)

        return CategoryUpdateResponse(id, "카테고리 수정 완료")
    }

    @Transactional
    fun deleteCategory(id: Long): CategoryDeleteResponse {
        val category = getCategory(id)

        categoryRepository.delete(category)

        return CategoryDeleteResponse(id, "카테고리 삭제 완료")
    }
}