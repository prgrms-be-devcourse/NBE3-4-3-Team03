package com.programmers.pcquotation.domain.item.service;

import org.springframework.stereotype.Service;

import com.programmers.pcquotation.domain.category.entity.Category;
import com.programmers.pcquotation.domain.category.repository.CategoryRepository;
import com.programmers.pcquotation.domain.item.dto.ItemCreateRequest;
import com.programmers.pcquotation.domain.item.dto.ItemCreateResponse;
import com.programmers.pcquotation.domain.item.dto.ItemDeleteResponse;
import com.programmers.pcquotation.domain.item.dto.ItemInfoResponse;
import com.programmers.pcquotation.domain.item.dto.ItemUpdateRequest;
import com.programmers.pcquotation.domain.item.dto.ItemUpdateResponse;
import com.programmers.pcquotation.domain.item.entity.Item;
import com.programmers.pcquotation.domain.item.exception.ItemNotFoundException;
import com.programmers.pcquotation.domain.item.repository.ItemRepository;

import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.repository.findByIdOrNull

@Service
class ItemService(
    private val itemRepository: ItemRepository,
    private val imageService: ImageService,
    private val categoryRepository: CategoryRepository
) {
    @Transactional
    @CacheEvict(value = ["items"], allEntries = true)
    fun addItem(request: ItemCreateRequest): ItemCreateResponse {
        val category: Category = categoryRepository.findByIdOrNull(request.categoryId)
            ?: throw IllegalArgumentException("유효하지 않은 카테고리 ID입니다.")

        val filename = imageService.storeImage(request.image)

        val item = Item(
            name = request.name,
            imgFilename = filename,
            category = category
        )

        val savedItem = itemRepository.save(item)

        val id = savedItem.id ?: throw IllegalStateException("부품 생성 후 ID가 null입니다.")
        return ItemCreateResponse(id, "부품 생성 완료")
    }

    @Transactional
    @Cacheable(value = ["items"], key = "'id'")
    fun getItemList(): List<ItemInfoResponse> {
        return itemRepository.findAll().map { toItemInfoResponse(it) }
    }

    @Transactional
    @Cacheable(value = ["items"], key = "#categoryId")
    fun getItemsByCategory(categoryId: Long): List<ItemInfoResponse> {
        return itemRepository.findByCategoryId(categoryId).map { toItemInfoResponse(it) }
    }

    @Transactional
    @CacheEvict(value = ["items"], allEntries = true)
    fun updateItem(id: Long, request: ItemUpdateRequest): ItemUpdateResponse {
        val item: Item = itemRepository.findByIdOrNull(id)
            ?: throw ItemNotFoundException(id)

        val category: Category = categoryRepository.findByIdOrNull(request.categoryId)
            ?: throw IllegalArgumentException("유효하지 않은 카테고리 ID입니다.")

        val imgFilename = request.imgFilename.takeIf { !it.isNullOrEmpty() } ?: item.imgFilename

        item.updateItem(
            request.name,
            imgFilename,
            category
        )

        return ItemUpdateResponse(id, "부품 수정 완료")
    }

    @Transactional
    @CacheEvict(value = ["items"], allEntries = true)
    fun deleteItem(id: Long): ItemDeleteResponse {
        val item: Item = itemRepository.findByIdOrNull(id)
            ?: throw ItemNotFoundException(id)

        itemRepository.delete(item)

        return ItemDeleteResponse(id, "부품 삭제 완료")
    }

    fun findByName(name: String): Item? {
        return itemRepository.findByName(name)
    }

    fun findById(id: Long): Item {
        return itemRepository.findByIdOrNull(id)
            ?: throw NoSuchElementException("존재하지 않는 아이템입니다.")
    }

    private fun toItemInfoResponse(item: Item): ItemInfoResponse {
        return ItemInfoResponse(
            id = item.id!!,
            name = item.name,
            categoryId = item.category.id,
            categoryName = item.category.category,
            filename = item.imgFilename
        )
    }
}