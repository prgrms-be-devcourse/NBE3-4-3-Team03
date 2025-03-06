package com.programmers.pcquotation.domain.seller.service

import com.programmers.pcquotation.domain.member.entitiy.Member
import com.programmers.pcquotation.domain.seller.dto.SellerUpdateDto
import com.programmers.pcquotation.domain.seller.entitiy.Seller
import com.programmers.pcquotation.domain.seller.repository.SellerRepository
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import java.util.*


@Service
@RequiredArgsConstructor
class SellerService(
    private val sellerRepository: SellerRepository,
) {


    fun findByUserName(name: String): Optional<Seller> {
        return sellerRepository.findByUsername(name)
    }

    fun createSeller(seller: Seller): Seller {
        sellerRepository.save(seller)
        return seller
    }

    fun modify(seller: Seller, sellerUpdateDto: SellerUpdateDto): Seller {
        if (seller.password == sellerUpdateDto.password) {
            if (sellerUpdateDto.userName!!.isNotEmpty()) seller.username = sellerUpdateDto.userName
            if (sellerUpdateDto.companyName!!.isNotEmpty()) seller.companyName = sellerUpdateDto.companyName
            if (sellerUpdateDto.newPassword!!.isNotEmpty()) {
                if (sellerUpdateDto.newPassword == sellerUpdateDto.confirmNewPassword) seller.password =
                    sellerUpdateDto.newPassword
                else throw NoSuchElementException("비밀번호가 일치하지않습니다.")
            }
        }
        sellerRepository.save(seller)
        return seller
    }

    fun findById(id: Long): Optional<Member> {
        return sellerRepository.findById(id).map { seller: Seller? -> seller }
    }

    fun findByApiKey(apiKey: String): Optional<Member> {
        return sellerRepository.findByApiKey(apiKey).map { seller: Seller? -> seller }
    }


    fun findSellerByUsername(username: String): Optional<Seller> {
        return sellerRepository.findByUsername(username)
    }

    fun findSellerByEmail(email: String): Optional<Seller> {
        return sellerRepository.findByEmail(email)
    }
}