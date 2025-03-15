package com.programmers.pcquotation.domain.member.controller

import com.programmers.pcquotation.domain.customer.service.CustomerService
import com.programmers.pcquotation.domain.seller.service.SellerService
import com.programmers.pcquotation.util.Util
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var sellerService: SellerService

    @Autowired
    private lateinit var customerService: CustomerService

    private val customerId: String = "customer123456"
    private val sellerId: String = "seller12345"
    private val ps: String = "password1234"

    @Test
    @Transactional
    @DisplayName("회원 회원가입")
    @Throws(Exception::class)
    fun t1() {
        Util.registerCustomer(customerId, ps, mvc, customerService)
    }

    @Test
    @Transactional
    @DisplayName("판매자 회원가입")
    @Throws(Exception::class)
    fun t2() {
        Util.registerSeller(sellerId, ps, mvc, sellerService)
    }

    @Test
    @DisplayName("JWT 회원 로그인")
    @WithMockUser(username = "test1234", roles = ["CUSTOMER"])
    @Throws(Exception::class)
    fun t3() {
        val responseBody = Util.loginCustomer(customerId, ps, mvc, customerService)
        val parts = responseBody.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        Assertions.assertEquals(3, parts.size, "응답 형식이 올바르지 않음")
        Assertions.assertFalse(parts[0].isEmpty(), "JWT 토큰이 비어 있음")
        Assertions.assertFalse(parts[1].isEmpty(), "API 키가 비어 있음")
        Assertions.assertFalse(parts[2].isEmpty(), "회원 유형이 비어 있음")
    }

    @Test
    @DisplayName("JWT 판매자 로그인")
    @WithMockUser(username = "test1234", roles = ["SELLER"])
    @Throws(Exception::class)
    fun t4() {
        val responseBody = Util.loginSeller(sellerId, ps, mvc, sellerService)
        val parts = responseBody.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        Assertions.assertEquals(3, parts.size, "응답 형식이 올바르지 않음")
        Assertions.assertFalse(parts[0].isEmpty(), "JWT 토큰이 비어 있음")
        Assertions.assertFalse(parts[1].isEmpty(), "API 키가 비어 있음")
        Assertions.assertFalse(parts[2].isEmpty(), "회원 유형이 비어 있음")
    }
}