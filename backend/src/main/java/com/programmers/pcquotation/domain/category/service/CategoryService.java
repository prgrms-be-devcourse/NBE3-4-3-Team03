package com.programmers.pcquotation.domain.category.service;

import java.util.List;
import java.util.stream.Collectors;

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
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;

	// 카테고리 생성
	@Transactional
	public CategoryCreateResponse addCategory(final CategoryCreateRequest request) {
		Category category = Category.builder()
			.category(request.category())
			.build();

		Category savedCategory = categoryRepository.save(category);

		return new CategoryCreateResponse(savedCategory.getId(), "카테고리 생성 완료");
	}

	// 카테고리 전체 조회
	public List<CategoryInfoResponse> getCategoryList() {
		return categoryRepository.findAll().stream()
			.map(category -> CategoryInfoResponse.builder()
				.id(category.getId())
				.category(category.getCategory())
				.build())
			.collect(Collectors.toList());
	}

	// 카테고리 수정
	@Transactional
	public CategoryUpdateResponse updateCategory(Long id, CategoryUpdateRequest request) {
		Category category = categoryRepository.findById(id)
			.orElseThrow(() -> new CategoryNotFoundException(id));

		category.updateCategory(request.category());

		categoryRepository.save(category);

		return new CategoryUpdateResponse(id, "카테고리 수정 완료");
	}

	//카테고리 삭제
	@Transactional
	public CategoryDeleteResponse deleteCategory(Long id) {
		Category category = categoryRepository.findById(id)
			.orElseThrow(() -> new CategoryNotFoundException(id));

		categoryRepository.delete(category);

		return new CategoryDeleteResponse(id, "카테고리 삭제 완료");
	}
}