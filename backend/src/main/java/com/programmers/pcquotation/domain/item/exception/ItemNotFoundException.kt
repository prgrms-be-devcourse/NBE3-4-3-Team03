package com.programmers.pcquotation.domain.item.exception;

public class ItemNotFoundException extends RuntimeException {
	public ItemNotFoundException(Long id) {
		super("부품을 찾을 수 없습니다. ID:" + id);
	}
}