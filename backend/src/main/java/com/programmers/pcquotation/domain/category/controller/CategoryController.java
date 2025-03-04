package com.programmers.pcquotation.domain.category.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.pcquotation.domain.category.dto.CategoryCreateRequest;
import com.programmers.pcquotation.domain.category.dto.CategoryCreateResponse;
import com.programmers.pcquotation.domain.category.dto.CategoryDeleteResponse;
import com.programmers.pcquotation.domain.category.dto.CategoryInfoResponse;
import com.programmers.pcquotation.domain.category.dto.CategoryUpdateRequest;
import com.programmers.pcquotation.domain.category.dto.CategoryUpdateResponse;
import com.programmers.pcquotation.domain.category.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/categories")
public class CategoryController {

	private final CategoryService categoryService;

	// 카테고리 추가
	@PostMapping
	public CategoryCreateResponse createCategory(
		@RequestBody CategoryCreateRequest request
	) {
		return categoryService.addCategory(request);
	}

	//카테고리 다건조회
	@GetMapping
	public List<CategoryInfoResponse> getCategoryList() {
		return categoryService.getCategoryList();
	}

	//카테고리 수정
	@PutMapping("/{id}")
	public CategoryUpdateResponse updateCategory(
		@PathVariable Long id,
		@RequestBody CategoryUpdateRequest request
	) {
		return categoryService.updateCategory(id, request);
	}

	//카테고리 삭제
	@DeleteMapping("/{id}")
	public CategoryDeleteResponse deleteCategory(
		@PathVariable Long id
	) {
		return categoryService.deleteCategory(id);

	}
}