package com.programmers.pcquotation.util

import com.programmers.pcquotation.domain.category.entity.Category
import com.programmers.pcquotation.domain.item.entity.Item

class TestItemFactory {

    companion object {
        @JvmStatic
        fun createTestItem(id: Long, name: String, imgFilename: String, category: Category): Item {
            return Item(id, name, imgFilename, category)
        }
    }
}