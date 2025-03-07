package com.programmers.pcquotation.util

import com.programmers.pcquotation.domain.category.entity.Category

class TestCategoryFactory {

    companion object {
        @JvmStatic
        fun createTestCategory(id: Long, categoryName: String): Category {
            return Category(id = id, category = categoryName)
        }
    }
}
