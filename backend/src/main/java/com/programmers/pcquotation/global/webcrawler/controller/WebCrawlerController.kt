package com.programmers.pcquotation.global.webcrawler.controller

import com.programmers.pcquotation.global.webcrawler.service.WebCrawlerService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class WebCrawlerController(
    private val webCrawlerService: WebCrawlerService
) {
    @GetMapping("/crawl-items")
    fun crawlItem() {
        webCrawlerService.saveCrawledItems()
    }
}