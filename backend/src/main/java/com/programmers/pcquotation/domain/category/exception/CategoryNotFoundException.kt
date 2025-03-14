package com.programmers.pcquotation.domain.category.exception;

public class CategoryNotFoundException extends RuntimeException {
	public CategoryNotFoundException(Long id) {
		super("카테고리를 찾을 수 없습니다. ID:" + id);
	}
}
