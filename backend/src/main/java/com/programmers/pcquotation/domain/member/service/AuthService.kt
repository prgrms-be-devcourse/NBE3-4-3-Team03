package com.programmers.pcquotation.domain.member.service

import com.programmers.pcquotation.domain.admin.service.AdminService
import com.programmers.pcquotation.domain.customer.dto.CustomerSignupRequest
import com.programmers.pcquotation.domain.customer.dto.CustomerSignupResponse
import com.programmers.pcquotation.domain.customer.entity.Customer
import com.programmers.pcquotation.domain.customer.exception.CustomerAlreadyExistException
import com.programmers.pcquotation.domain.customer.exception.IncorrectLoginAttemptException
import com.programmers.pcquotation.domain.customer.exception.PasswordMismatchException
import com.programmers.pcquotation.domain.customer.service.CustomerService
import com.programmers.pcquotation.domain.member.dto.AuthRequest
import com.programmers.pcquotation.domain.member.dto.LoginRequest
import com.programmers.pcquotation.domain.member.dto.LoginResponse
import com.programmers.pcquotation.domain.member.entitiy.Member
import com.programmers.pcquotation.domain.seller.dto.SellerSignupRequest
import com.programmers.pcquotation.domain.seller.dto.SellerSignupResponse
import com.programmers.pcquotation.domain.seller.entitiy.Seller
import com.programmers.pcquotation.domain.seller.service.SellerService
import com.programmers.pcquotation.global.enums.UserType
import com.programmers.pcquotation.global.enums.UserType.*
import com.programmers.pcquotation.global.jwt.Jwt
import com.programmers.pcquotation.global.rq.Rq

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthService(
    @Value("\${jwt.secretKey}")
    private val jwtSecretKey: String,
    @Value("\${jwt.accessToken.expirationSeconds}")
    private val accessTokenExpirationSeconds: Long,
    private val customerService: CustomerService,
    private val sellerService: SellerService,
    private val adminService: AdminService,
    private val passwordEncoder: PasswordEncoder,
    private val rq: Rq
) {


    fun processSignup(customerSignupRequest: CustomerSignupRequest): CustomerSignupResponse {
        if (customerSignupRequest.password != customerSignupRequest.confirmPassword) {
            throw PasswordMismatchException()
        }

        if (customerService.findCustomerByUsername(customerSignupRequest.username).isPresent) {
            throw CustomerAlreadyExistException()
        }

        if (customerService.findCustomerByEmail(customerSignupRequest.email).isPresent) {
            throw CustomerAlreadyExistException()
        }

        val customer = Customer(
            customerSignupRequest,
            UUID.randomUUID().toString(),
            passwordEncoder.encode(customerSignupRequest.password)
        )

        customerService.createCustomer(customer)
        return CustomerSignupResponse(customer, "회원가입 성공")
    }

    fun processSignup(sellerSignupRequest: SellerSignupRequest): SellerSignupResponse {
        if (sellerSignupRequest.password != sellerSignupRequest.confirmPassword) {
            throw PasswordMismatchException()
        }

        if (sellerService.findSellerByUsername(sellerSignupRequest.username).isPresent) {
            throw CustomerAlreadyExistException()
        }

        if (sellerService.findSellerByEmail(sellerSignupRequest.email).isPresent) {
            throw CustomerAlreadyExistException()
        }

        val seller = Seller(
            sellerSignupRequest,
            UUID.randomUUID().toString(),
            passwordEncoder.encode(sellerSignupRequest.password)
        )
        sellerService.createSeller(seller)

        return SellerSignupResponse(seller, "회원가입 성공")
    }

    fun processLoginCustomer(customerLoginRequest: LoginRequest): LoginResponse? {
        val username = customerLoginRequest.username
        val customer = customerService.findCustomerByUsername(username)
            .orElseThrow { IncorrectLoginAttemptException() }

        if (!passwordEncoder.matches(customerLoginRequest.password, customer.password)) {
            throw IncorrectLoginAttemptException()
        }

        val accessToken = this.getAccessToken(customer)
        rq.setCookie("accessToken", accessToken)
        rq.setCookie("apiKey", customer.apiKey ?: throw Exception("ApiKey가 존재하지 않습니다."))
        rq.setCookie("userType", CUSTOMER.value)
        return customer.apiKey?.let {
            LoginResponse(
                it,
                accessToken,
                SELLER,
                "로그인 성공"
            )
        }
    }

    fun processLoginSeller(loginRequest: LoginRequest): LoginResponse {
        val username = loginRequest.username
        val seller = sellerService.findByUserName(username)
            .orElseThrow { IncorrectLoginAttemptException() }

        if (!passwordEncoder.matches(loginRequest.password, seller.password)) {
            throw IncorrectLoginAttemptException()
        }
        val accessToken = this.getAccessToken(seller)
        rq.setCookie("accessToken", accessToken)
        rq.setCookie("apiKey", seller.apiKey ?: throw Exception("ApiKey가 존재하지 않습니다."))
        rq.setCookie("userType", SELLER.value)

        return seller.apiKey?.let {
            LoginResponse(
                it,
                accessToken,
                SELLER,
                "로그인 성공"
            )
        } ?: throw Exception()
    }

    fun processLoginAdmin(loginRequest: LoginRequest): LoginResponse {
        val username = loginRequest.username
        val admin = adminService.findAdminByUsername(username)
            .orElseThrow { IncorrectLoginAttemptException() }
        if (!passwordEncoder.matches(loginRequest.password, admin.password)) {
            throw IncorrectLoginAttemptException()
        }
        val accessToken = this.getAccessToken(admin)
        rq.setCookie("accessToken", accessToken)
        rq.setCookie("apiKey", admin.apiKey?: throw Exception("ApiKey가 존재하지 않습니다."))
        rq.setCookie("userType", ADMIN.value)


        return admin.apiKey?.let {
            LoginResponse(
                it,
                accessToken,
                ADMIN,
                "로그인 성공"
            )
        } ?: throw Exception()
    }

    val memberFromRq: AuthRequest
        get() {
            val member = rq.getMember()
            val auth = member.authorities
            val userType = UserType.fromString(auth.toString()).toString()
            return AuthRequest(userType)
        }

    fun getMemberFromAccessToken(accessToken: String, userType: UserType): Optional<Member> {
        val payload = this.payload(accessToken) ?: return Optional.empty()

        val id = payload["id"] as Long
        return when (userType) {
            SELLER -> {
                sellerService.findById(id)
            }

            CUSTOMER -> {
                customerService.findById(id)
            }

            ADMIN -> {
                adminService.findById(id)
            }

            NOTHING -> Optional.empty()
        }
    }

    fun getAccessToken(member: Member): String {
        val id = member.id
        val username = member.username
        return Jwt.toString(
            jwtSecretKey,
            accessTokenExpirationSeconds,
            java.util.Map.of<String, Any?>("id", id, "username", username)
        )
    }

    fun findByApiKey(apiKey: String, userType: UserType): Optional<Member> {
        return when (userType) {
            CUSTOMER -> {
                customerService.findByApiKey(apiKey)
            }

            SELLER -> {
                sellerService.findByApiKey(apiKey)
            }

            ADMIN -> {
                adminService.findByApiKey(apiKey)
            }

            NOTHING -> Optional.empty()
        }

    }

    private fun payload(accessToken: String): Map<String, Any>? {
        val parsedPayload = Jwt.payload(jwtSecretKey, accessToken) ?: return null

        val id = (parsedPayload["id"] as Int?)!!.toLong()
        val username = parsedPayload["username"] as String?

        return java.util.Map.of<String, Any>("id", id, "username", username)
    }

    fun processLogout() {
        rq.deleteCookie("accessToken")
        rq.deleteCookie("apiKey")
        rq.deleteCookie("userType")
    }
}