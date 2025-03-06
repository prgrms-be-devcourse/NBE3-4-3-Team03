package com.programmers.pcquotation.domain.item.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import com.programmers.pcquotation.domain.category.entity.Category;
import com.programmers.pcquotation.domain.category.repository.CategoryRepository;
import com.programmers.pcquotation.domain.estimate.repository.EstimateRepository;
import com.programmers.pcquotation.domain.item.dto.ItemCreateRequest;
import com.programmers.pcquotation.domain.item.dto.ItemCreateResponse;
import com.programmers.pcquotation.domain.item.dto.ItemDeleteResponse;
import com.programmers.pcquotation.domain.item.dto.ItemUpdateRequest;
import com.programmers.pcquotation.domain.item.dto.ItemUpdateResponse;
import com.programmers.pcquotation.domain.item.entity.Item;
import com.programmers.pcquotation.domain.item.repository.ItemRepository;
@ActiveProfiles("test")
class ItemServiceTest {
	@InjectMocks // ItemService에 Mock 객체 자동 주입
	private ItemService itemService;

	@Mock
	private EstimateRepository estimateRepository;

	@Mock
	private ItemRepository itemRepository;

	@Mock
	private ImageService imageService;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private MultipartFile mockFile;

	private Item item;
	private Category oldCategory;
	private Category newCategory;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		oldCategory = Category.createTestCategory(1L, "기존 카테고리");
		newCategory = Category.createTestCategory(2L, "새로운 카테고리");

		item = Item.createTestItem(
			1L,
			"기존 부품 이름",
			"old-image.jpg",
			oldCategory
		);
	}

	@Test
	@DisplayName("addItem 데이터 추가, 저장 테스트")
	void addItemTest() {
		// Given
		String expectedImagePath = "uploads/img.png";
		when(imageService.storeImage(any(MultipartFile.class))).thenReturn(expectedImagePath);

		// 카테고리 Mock 객체 생성
		Category mockCategory = mock(Category.class);
		when(mockCategory.getId()).thenReturn(2L);
		when(categoryRepository.findById(2L)).thenReturn(Optional.of(mockCategory));

		ItemCreateRequest request = new ItemCreateRequest(2L, "부품", mockFile);
		Item item = Item.builder()
			.category(mockCategory)
			.name("부품")
			.imgFilename(expectedImagePath)
			.build();
		Item savedItem = Item.builder()
			.id(2L)
			.category(mockCategory)
			.name("부품")
			.imgFilename(expectedImagePath)
			.build();

		when(itemRepository.save(any(Item.class))).thenReturn(savedItem);

		// When
		ItemCreateResponse response = itemService.addItem(request);

		// Then
		assertThat(response.id()).isEqualTo(2L);
		assertThat(response.message()).isEqualTo("부품 생성 완료");
	}

	@Test
	@DisplayName("부품 조회 테스트")
	void getItemListTest() {
		// 테스트용 카테고리 생성
		Category testCategory = Category.createTestCategory(1L, "GPU");

		// 테스트용 부품 생성
		Item item = Item.builder()
			.name("4090")
			.imgFilename("gpu.jpg")
			.category(testCategory)
			.build();

		// 검증
		assertNotNull(item);
		assertEquals("4090", item.getName());
		assertEquals("gpu.jpg", item.getImgFilename());
		assertEquals("GPU", item.getCategory().getCategory());
	}

	@Test
	@DisplayName("부품 수정 테스트")
	void updateItemTest() {
		// Given
		ItemUpdateRequest request = new ItemUpdateRequest("새로운 부품 이름", "new-image.jpg", 2L);

		when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
		when(categoryRepository.findById(2L)).thenReturn(Optional.of(newCategory));

		// When
		ItemUpdateResponse response = itemService.updateItem(1L, request);

		// Then
		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.message()).isEqualTo("부품 수정 완료");

		assertThat(item.getName()).isEqualTo("새로운 부품 이름");
		assertThat(item.getImgFilename()).isEqualTo("new-image.jpg");
		assertThat(item.getCategory()).isEqualTo(newCategory);

		verify(itemRepository, times(1)).findById(1L);
		verify(categoryRepository, times(1)).findById(2L);
	}

	@Test
	@DisplayName("부품 삭제 테스트")
	void deleteItemTest() {
		// Given
		Long itemId = 1L;
		Item item = Item.createTestItem(itemId, "부품1", "image.png", null); // 아이템 객체 생성
		when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

		// When
		ItemDeleteResponse response = itemService.deleteItem(itemId);

		// Then
		assertThat(response.id()).isEqualTo(itemId);
		assertThat(response.message()).isEqualTo("부품 삭제 완료");

		verify(itemRepository, times(1)).findById(itemId);
		// verify(estimateRepository, times(1)).deleteComponentsByItemId(itemId); // EstimateComponent 삭제 검증
		verify(itemRepository, times(1)).delete(item); // 아이템 삭제 검증
	}
}