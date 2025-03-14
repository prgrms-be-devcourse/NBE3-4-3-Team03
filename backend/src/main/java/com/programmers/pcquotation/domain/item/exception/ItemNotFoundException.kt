package com.programmers.pcquotation.domain.item.exception

class ItemNotFoundException(id: Long) : RuntimeException("부품을 찾을 수 없습니다. ID:$id")