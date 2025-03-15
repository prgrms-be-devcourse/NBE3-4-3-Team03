package com.programmers.pcquotation.domain.seller.service

import com.programmers.pcquotation.domain.member.entity.Member
import com.programmers.pcquotation.domain.seller.dto.SellerUpdateDto
import com.programmers.pcquotation.domain.seller.entity.Seller
import com.programmers.pcquotation.domain.seller.repository.SellerRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class SellerService(
    private val sellerRepository: SellerRepository,
) {
    fun findByUserName(name: String): Seller? {
        return sellerRepository.findByUsername(name)
    }

    fun createSeller(seller: Seller): Seller {
        sellerRepository.save(seller)
        return seller
    }

    fun modify(seller: Seller, sellerUpdateDto: SellerUpdateDto): Seller {
        if (seller.password == sellerUpdateDto.password) {
            if (sellerUpdateDto.userName.isNotEmpty()) seller.username = sellerUpdateDto.userName
            if (sellerUpdateDto.companyName.isNotEmpty()) seller.companyName = sellerUpdateDto.companyName
            if (sellerUpdateDto.newPassword.isNotEmpty()) {
                if (sellerUpdateDto.newPassword == sellerUpdateDto.confirmNewPassword) seller.password =
                    sellerUpdateDto.newPassword
                else throw NoSuchElementException("비밀번호가 일치하지않습니다.")
            }
        }
        sellerRepository.save(seller)
        return seller
    }

    fun findById(id: Long): Member {
        return sellerRepository.findByIdOrNull(id)
            ?: throw NoSuchElementException()
    }

    fun findByApiKey(apiKey: String): Member? {
        return sellerRepository.findByApiKey(apiKey)
    }

    fun findSellerByUsername(username: String): Seller? {
        return sellerRepository.findByUsername(username)
    }

    fun findSellerByEmail(email: String): Seller? {
        return sellerRepository.findByEmail(email)
    }
}