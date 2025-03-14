package com.programmers.pcquotation.domain.item.service

import com.programmers.pcquotation.domain.category.entity.Category
import com.programmers.pcquotation.domain.category.repository.CategoryRepository
import com.programmers.pcquotation.domain.estimate.repository.EstimateRepository
import com.programmers.pcquotation.domain.item.dto.ItemCreateRequest
import com.programmers.pcquotation.domain.item.dto.ItemUpdateRequest
import com.programmers.pcquotation.domain.item.entity.Item
import com.programmers.pcquotation.domain.item.repository.ItemRepository
import com.programmers.pcquotation.util.TestCategoryFactory.Companion.createTestCategory
import com.programmers.pcquotation.util.TestItemFactory.Companion.createTestItem
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.*
import org.mockito.invocation.InvocationOnMock
import org.mockito.kotlin.any
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.multipart.MultipartFile
import java.util.*

@ActiveProfiles("test")
internal class ItemServiceTest {
    @InjectMocks
    private lateinit var itemService: ItemService

    @Mock
    private lateinit var estimateRepository: EstimateRepository

    @Mock
    private lateinit var itemRepository: ItemRepository

    @Mock
    private lateinit var imageService: ImageService

    @Mock
    private lateinit var categoryRepository: CategoryRepository

    @Mock
    private lateinit var mockFile: MultipartFile

    private val oldCategory = createTestCategory(1L, "기존 카테고리")
    private val newCategory = createTestCategory(2L, "새로운 카테고리")
    private val item = createTestItem(
        1L,
        "기존 부품 이름",
        "old-image.jpg",
        oldCategory
    )

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    @DisplayName("addItem 데이터 추가, 저장 테스트")
    fun addItemTest() {
        val expectedImagePath = "uploads/img.png"
        Mockito.`when`(imageService.storeImage(any()))
            .thenReturn(expectedImagePath)

        val mockCategory = Mockito.mock(
            Category::class.java
        )
        Mockito.`when`(mockCategory.id).thenReturn(2L)
        Mockito.`when`(
            categoryRepository.findById(2L)
        ).thenReturn(Optional.of(mockCategory))

        val request = ItemCreateRequest(2L, "부품", mockFile)

        val itemToSave = Item(null, "부품", expectedImagePath, mockCategory, ArrayList())

        Mockito.`when`(
            itemRepository.save(
                ArgumentMatchers.any(
                    Item::class.java
                )
            )
        ).thenAnswer { invocation: InvocationOnMock ->
            val item =
                invocation.getArgument<Item>(0)
            Item(
                1L, item.name, item.imgFilename, item.category,
                item.estimateComponents
            )
        }

        val response = itemService.addItem(request)

        Assertions.assertThat(response.id).isEqualTo(1L) // Mock에서 반환된 ID 검증
        Assertions.assertThat(response.message).isEqualTo("부품 생성 완료")
    }

    @DisplayName("부품 조회 테스트")
    @Test
    fun getItemListTest() {
            val testCategory =
                createTestCategory(1L, "GPU")

            val item = Item(
                name = "4090",
                imgFilename = "gpu.jpg",
                category = testCategory
            )

            org.junit.jupiter.api.Assertions.assertNotNull(item)
            org.junit.jupiter.api.Assertions.assertEquals("4090", item.name)
            org.junit.jupiter.api.Assertions.assertEquals("gpu.jpg", item.imgFilename)
            org.junit.jupiter.api.Assertions.assertEquals("GPU", item.category.category)
        }

    @Test
    @DisplayName("부품 수정 테스트")
    fun updateItemTest() {
        val request = ItemUpdateRequest("새로운 부품 이름", "new-image.jpg", 2L)

        Mockito.`when`(itemRepository.findById(1L)).thenReturn(
            Optional.of(
                item
            )
        )
        Mockito.`when`(
            categoryRepository.findById(2L)
        ).thenReturn(
            Optional.of(
                newCategory
            )
        )

        val response = itemService.updateItem(1L, request)

        Assertions.assertThat(response.id).isEqualTo(1L)
        Assertions.assertThat(response.message).isEqualTo("부품 수정 완료")

        Assertions.assertThat(item.name).isEqualTo("새로운 부품 이름")
        Assertions.assertThat(item.imgFilename).isEqualTo("new-image.jpg")
        Assertions.assertThat(item.category).isEqualTo(newCategory)

        Mockito.verify(itemRepository, Mockito.times(1)).findById(1L)
        Mockito.verify(categoryRepository, Mockito.times(1)).findById(2L)
    }

    @Test
    @DisplayName("부품 삭제 테스트")
    fun deleteItemTest() {
        val itemId = 1L
        val mockCategory = Category(
            category = "부품 카테고리"
        )

        val item = createTestItem(itemId, "부품1", "image.png", mockCategory)
        Mockito.`when`(itemRepository.findById(itemId)).thenReturn(Optional.of(item))

        val response = itemService.deleteItem(itemId)

        Assertions.assertThat(response.id).isEqualTo(itemId)
        Assertions.assertThat(response.message).isEqualTo("부품 삭제 완료")

        Mockito.verify(itemRepository, Mockito.times(1)).findById(itemId)

        Mockito.verify(itemRepository, Mockito.times(1)).delete(item)
    }
}