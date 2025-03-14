package com.programmers.pcquotation.domain.category.service

import com.programmers.pcquotation.domain.category.dto.CategoryCreateRequest
import com.programmers.pcquotation.domain.category.dto.CategoryInfoResponse
import com.programmers.pcquotation.domain.category.dto.CategoryUpdateRequest
import com.programmers.pcquotation.domain.category.entity.Category
import com.programmers.pcquotation.domain.category.repository.CategoryRepository
import com.programmers.pcquotation.util.TestCategoryFactory.Companion.createTestCategory
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.test.context.ActiveProfiles
import java.util.*

@ActiveProfiles("test")
class CategoryServiceTest {
    private lateinit var categoryService: CategoryService

    @Mock
    private lateinit var categoryRepository: CategoryRepository

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        categoryService = CategoryService(categoryRepository)
    }

    @Test
    @DisplayName("addCategory 데이터 추가, 저장 테스트")
    fun addCategoryTest() {
        val request = CategoryCreateRequest("카테고리")
        val savedCategory = Category(1L, "카테고리", ArrayList()) // items는 기본값인 빈 리스트로 설정됨

        Mockito.`when`<Any>(
            categoryRepository.save(ArgumentMatchers.argThat { category: Category -> category.category == "카테고리" })
        )
            .thenReturn(savedCategory)

        val response = categoryService.addCategory(request)

        Assertions.assertThat(response.id).isEqualTo(1L)
        Assertions.assertThat(response.message).isEqualTo("카테고리 생성 완료")
    }

    @DisplayName("카테고리 다건조회 테스트")
    @Test
    fun getCategoryListTest() {
            val category1 =
                Category(
                    1L,
                    "카테고리",
                    ArrayList()
                )
            val category2 =
                Category(
                    2L,
                    "카테고리2",
                    ArrayList()
                )

            Mockito.`when`(
                categoryRepository.findAll()
            ).thenReturn(
                listOf(
                    category1,
                    category2
                )
            )

            val response = categoryService.getCategoryList()

            val expectedList = listOf(
                CategoryInfoResponse(1L, "카테고리"),
                CategoryInfoResponse(2L, "카테고리2")
            )

            Assertions.assertThat(response).hasSize(expectedList.size)
            for (i in response.indices) {
                Assertions.assertThat(response[i].id).isEqualTo(expectedList[i].id)
                Assertions.assertThat(response[i].category)
                    .isEqualTo(expectedList[i].category)
            }
        }

    @Test
    @DisplayName("카테고리 수정 테스트")
    fun updateCategoryTest() {
        val categoryId = 1L
        val existingCategory = createTestCategory(categoryId, "기존 카테고리")
        val updateRequest = CategoryUpdateRequest("수정된 카테고리")

        Mockito.`when`(
            categoryRepository.findById(categoryId)
        ).thenReturn(Optional.of(existingCategory))

        categoryService.updateCategory(categoryId, updateRequest)

        Assertions.assertThat(existingCategory.category).isEqualTo("수정된 카테고리")
    }

    @Test
    @DisplayName("카테고리 삭제 테스트")
    fun deleteCategoryTest() {
        val categoryId = 1L
        val existingCategory = createTestCategory(1L, "기존 카테고리")
        Mockito.`when`(
            categoryRepository.findById(categoryId)
        ).thenReturn(Optional.of(existingCategory))

        val response = categoryService.deleteCategory(categoryId)

        Assertions.assertThat(response.id).isEqualTo(categoryId)
        Assertions.assertThat(response.message).isEqualTo("카테고리 삭제 완료")
        Mockito.verify(categoryRepository).delete(
            ArgumentMatchers.any(
                Category::class.java
            )
        )
    }
}