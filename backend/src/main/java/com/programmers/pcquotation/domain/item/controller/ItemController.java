package com.programmers.pcquotation.domain.item.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.pcquotation.domain.item.dto.ItemCreateRequest;
import com.programmers.pcquotation.domain.item.dto.ItemCreateResponse;
import com.programmers.pcquotation.domain.item.dto.ItemDeleteResponse;
import com.programmers.pcquotation.domain.item.dto.ItemInfoResponse;
import com.programmers.pcquotation.domain.item.dto.ItemUpdateRequest;
import com.programmers.pcquotation.domain.item.dto.ItemUpdateResponse;
import com.programmers.pcquotation.domain.item.service.ItemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/items")

public class ItemController {

	private final ItemService itemService;

	@PostMapping
	public ItemCreateResponse createItem( //부품 생성
		@ModelAttribute("request") ItemCreateRequest request
	) {
		return itemService.addItem(request);
	}

	@GetMapping
	public List<ItemInfoResponse> getInfoList(@RequestParam(required = false) Long categoryId) {
		if (categoryId != null) {
			return itemService.getItemsByCategory(categoryId);
		}
		return itemService.getItemList(); // 기존 방식 유지
	}

	@PutMapping("/{id}")
	public ItemUpdateResponse updateItem( //부품 수정
		@PathVariable Long id,
		@ModelAttribute("request") ItemUpdateRequest request
	) {
		return itemService.updateItem(id, request);
	}

	@DeleteMapping("/{id}")
	public ItemDeleteResponse deleteItem(
		@PathVariable Long id
	) {
		return itemService.deleteItem(id);
	}
}
