package com.programmers.pcquotation.global.webcrawler.service

import com.opencsv.CSVReader
import com.programmers.pcquotation.domain.category.service.CategoryService
import com.programmers.pcquotation.domain.item.entity.Item
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.jsoup.Jsoup
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.StringReader

@Service
class WebCrawlerService(
    private val categoryService: CategoryService
) {
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    private val baseUrl =
        "https://raw.githubusercontent.com/SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSun/Danawa-Crawler/master/crawl_data/"

    private val pcCategories = listOf(
        "CPU", "Mainboard", "RAM",
        "Air Cooler", "AIO Cooler",
        "VGA", "SSD", "HDD", "Case", "PSU"
    )

    @Transactional
    fun saveCrawledItems() {
        val batchSize = 500
        var count = 0

        pcCategories.forEach { categoryName ->
            val category = categoryService.addCategory(categoryName)
            val results = crawlWebsite(categoryName)

            results.distinctBy { result -> result[1] }
                .map { result ->
                    val itemName = result[1]

                    val item = Item(
                        name = itemName,
                        category = category,
                        imgFilename = "test.jpg"
                    )

                    entityManager.persist(item)
                    count++

                    if (count % batchSize == 0) {
                        entityManager.flush()
                        entityManager.clear()
                    }
                }

            entityManager.flush()
            entityManager.clear()
        }
    }

    private fun crawlWebsite(categoryName: String): List<Array<String>> {
        val rawData = fetchCsvFromUrl("$baseUrl$categoryName.csv")
        return parseCsvData(rawData)
    }

    private fun fetchCsvFromUrl(csvUrl: String): String {
        return Jsoup.connect(csvUrl).ignoreContentType(true).execute().body()
    }

    private fun parseCsvData(csvData: String): List<Array<String>> {
        val csvReader = CSVReader(StringReader(csvData))
        csvReader.readNext()

        val records = csvReader.readAll()
        csvReader.close()

        return records
    }
}