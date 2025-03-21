package com.programmers.pcquotation.domain.seller.controller

import com.programmers.pcquotation.domain.seller.dto.SellerInfoResponse
import com.programmers.pcquotation.domain.seller.dto.SellerUpdateDto
import com.programmers.pcquotation.domain.seller.service.BusinessConfirmationService
import com.programmers.pcquotation.domain.seller.service.SellerService
import jakarta.validation.Valid
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/seller")
class SellerController(
    private val sellerService: SellerService,
    private val businessConfirmationService: BusinessConfirmationService
) {
    @GetMapping
    @Transactional(readOnly = true)
    fun info(principal: Principal): SellerInfoResponse {
        val seller = sellerService
            .findByUserName(principal.name)
            ?: throw NullPointerException("존재하지 않는 사용자입니다.")

        return SellerInfoResponse(
            seller.id,
            seller.username,
            seller.companyName,
            seller.email
        )
    }

    @PutMapping
    fun modify(@RequestBody customerUpdateDto: @Valid SellerUpdateDto, principal: Principal): String {
        val seller = sellerService
            .findByUserName(principal.name)
            ?: throw NullPointerException("존재하지 않는 사용자입니다.")

        sellerService.modify(seller, customerUpdateDto)
        return "정보수정이 성공했습니다."
    }

    @GetMapping("/business/{code}/check")
    @Transactional(readOnly = true)
    fun checkCode(@PathVariable("code") code: String): String {
        if (businessConfirmationService.checkCode(code)) {
            return "true"
        }
        return "false"
    }
}