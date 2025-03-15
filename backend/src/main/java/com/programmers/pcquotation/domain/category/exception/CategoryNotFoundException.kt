package com.programmers.pcquotation.domain.category.exception

class CategoryNotFoundException(
    id: Long
) : RuntimeException("카테고리를 찾을 수 없습니다. ID:$id")