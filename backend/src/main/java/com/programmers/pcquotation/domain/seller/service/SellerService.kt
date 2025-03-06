package com.programmers.pcquotation.domain.seller.service

import com.programmers.pcquotation.domain.member.entitiy.Member
import com.programmers.pcquotation.domain.seller.dto.SellerUpdateDto
import com.programmers.pcquotation.domain.seller.entitiy.Seller
import com.programmers.pcquotation.domain.seller.repository.SellerRepository
import lombok.RequiredArgsConstructor
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*


@Service
@RequiredArgsConstructor
class SellerService(
    private val sellerRepository: SellerRepository,
    private val passwordEncoder: PasswordEncoder
) {


    fun findByUserName(name: String): Optional<Seller> {
        return sellerRepository.findByUsername(name)
    }

    fun createSeller(seller: Seller): Seller {
        sellerRepository.save(seller)
        return seller
    }

    fun modify(seller: Seller, customerUpdateDto: SellerUpdateDto): Seller {
        if (seller.password == customerUpdateDto.password) {
            if (customerUpdateDto.userName!!.isNotEmpty()) seller.username = customerUpdateDto.userName
            if (customerUpdateDto.companyName!!.isNotEmpty()) seller.companyName = customerUpdateDto.companyName
            if (customerUpdateDto.newPassword!!.isNotEmpty()) {
                if (customerUpdateDto.newPassword == customerUpdateDto.confirmNewPassword) seller.password =
                    customerUpdateDto.newPassword
                else throw NoSuchElementException("비밀번호가 일치하지않습니다.")
            }
        }
        sellerRepository.save(seller)
        return seller
    }

    fun findById(id: Long): Optional<Member<*>> {
        return sellerRepository.findById(id).map { seller: Seller? -> seller }
    }

    fun findByApiKey(apiKey: String): Optional<Member<*>> {
        return sellerRepository.findByApiKey(apiKey).map { seller: Seller? -> seller }
    }


    fun findSellerByUsername(username: String): Optional<Seller> {
        return sellerRepository.findByUsername(username)
    }

    fun findSellerByEmail(email: String): Optional<Seller> {
        return sellerRepository.findByEmail(email)
    }
}