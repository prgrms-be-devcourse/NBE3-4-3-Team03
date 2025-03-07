package com.programmers.pcquotation.domain.item.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
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
import com.programmers.pcquotation.util.TestCategoryFactory;

@ActiveProfiles("test")
class ItemServiceTest {
	@InjectMocks
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
		oldCategory = TestCategoryFactory.createTestCategory(1L, "기존 카테고리");
		newCategory = TestCategoryFactory.createTestCategory(2L, "새로운 카테고리");

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

		String expectedImagePath = "uploads/img.png";
		when(imageService.storeImage(any(MultipartFile.class))).thenReturn(expectedImagePath);

		Category mockCategory = mock(Category.class);
		when(mockCategory.getId()).thenReturn(2L);
		when(categoryRepository.findById(2L)).thenReturn(Optional.of(mockCategory));

		ItemCreateRequest request = new ItemCreateRequest(2L, "부품", mockFile);

		Item itemToSave = new Item(null, "부품", expectedImagePath, mockCategory, new ArrayList<>());

		when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
			Item item = invocation.getArgument(0);

			return new Item(1L, item.getName(), item.getImgFilename(), item.getCategory(),
				item.getEstimateComponents());
		});

		ItemCreateResponse response = itemService.addItem(request);

		assertThat(response.id()).isEqualTo(1L); // Mock에서 반환된 ID 검증
		assertThat(response.message()).isEqualTo("부품 생성 완료");
	}

	@Test
	@DisplayName("부품 조회 테스트")
	void getItemListTest() {

		Category testCategory = TestCategoryFactory.createTestCategory(1L, "GPU");

		Item item = new Item();
		item.setName("4090");
		item.setImgFilename("gpu.jpg");
		item.setCategory(testCategory);

		assertNotNull(item);
		assertEquals("4090", item.getName());
		assertEquals("gpu.jpg", item.getImgFilename());
		assertEquals("GPU", item.getCategory().getCategory());
	}

	@Test
	@DisplayName("부품 수정 테스트")
	void updateItemTest() {

		ItemUpdateRequest request = new ItemUpdateRequest("새로운 부품 이름", "new-image.jpg", 2L);

		when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
		when(categoryRepository.findById(2L)).thenReturn(Optional.of(newCategory));

		ItemUpdateResponse response = itemService.updateItem(1L, request);

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

		Long itemId = 1L;
		Category mockCategory = new Category();
		mockCategory.setCategory("부품 카테고리");

		Item item = Item.createTestItem(itemId, "부품1", "image.png", mockCategory);
		when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

		ItemDeleteResponse response = itemService.deleteItem(itemId);

		assertThat(response.id()).isEqualTo(itemId);
		assertThat(response.message()).isEqualTo("부품 삭제 완료");

		verify(itemRepository, times(1)).findById(itemId);

		verify(itemRepository, times(1)).delete(item);
	}
}