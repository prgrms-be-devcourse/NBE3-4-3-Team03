package com.programmers.pcquotation.domain.member.service

import com.programmers.pcquotation.domain.customer.dto.CustomerSignupRequest
import com.programmers.pcquotation.domain.customer.entity.Customer
import com.programmers.pcquotation.domain.customer.exception.CustomerAlreadyExistException
import com.programmers.pcquotation.domain.customer.exception.IncorrectLoginAttemptException
import com.programmers.pcquotation.domain.customer.exception.PasswordMismatchException
import com.programmers.pcquotation.domain.customer.repository.CustomerRepository
import com.programmers.pcquotation.domain.member.dto.LoginRequest
import com.programmers.pcquotation.domain.member.service.AuthService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.util.*

@ActiveProfiles("test")
@SpringBootTest
class AuthServiceTest {
    @Autowired
    private lateinit var authService: AuthService

    @MockitoBean
    private lateinit var customerRepository: CustomerRepository

    @MockitoBean
    private lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun signup_passwordMismatch() {
        val customerSignupRequest = CustomerSignupRequest(
            "user1",
            "1234",
            "1111",
            "홍길동",
            "test@test.com",
            "가장 좋아하는 음식은",
            "밥"
        )

        Assertions.assertThrows(PasswordMismatchException::class.java) {
            authService.processCustomerSignup(customerSignupRequest)
        }
    }

    @Test
    fun signup_customerUserNameAlreadyExist() {
        val customerSignupRequest = CustomerSignupRequest(
            "user1",
            "1234",
            "1234",
            "홍길동",
            "test@test.com",
            "가장 좋아하는 음식은",
            "밥"
        )

        val customer = customerSignupRequest.toCustomer()
        Mockito.`when`(customerRepository.getCustomerByUsername(customerSignupRequest.username))
            .thenReturn(Optional.of(customer))

        Assertions.assertThrows(CustomerAlreadyExistException::class.java) {
            authService.processCustomerSignup(customerSignupRequest)
        }
    }

    @Test
    fun signup_customerEmailAlreadyExist() {
        val customerSignupRequest = CustomerSignupRequest(
            "user1",
            "1234",
            "1234",
            "홍길동",
            "test@test.com",
            "가장 좋아하는 음식은",
            "밥"
        )

        val customer = customerSignupRequest.toCustomer()
        Mockito.`when`(customerRepository.getCustomerByEmail(customerSignupRequest.email))
            .thenReturn(Optional.of(customer))

        Assertions.assertThrows(CustomerAlreadyExistException::class.java) {
            authService.processCustomerSignup(customerSignupRequest)
        }
    }


    @Test
    fun login_usernameNotFound() {
        val customerLoginRequest = LoginRequest(
            "user1",
            "1234"
        )

        Mockito.`when`(customerRepository.getCustomerByUsername("user1")).thenReturn(Optional.empty())

        Assertions.assertThrows(IncorrectLoginAttemptException::class.java) {
            authService.processLoginCustomer(customerLoginRequest)
        }

        val authentication = SecurityContextHolder.getContext().authentication
        Assertions.assertNull(authentication)
    }

    @Test
    fun login_incorrectPassword() {
        val customerLoginRequest = LoginRequest(
            "user1",
            "1234"
        )

        val customer = Customer(
            "user1",
            "1111"
        )

        Mockito.`when`(customerRepository.getCustomerByUsername("user1")).thenReturn(Optional.of(customer))

        Assertions.assertThrows(IncorrectLoginAttemptException::class.java) {
            authService.processLoginCustomer(customerLoginRequest)
        }

        val authentication = SecurityContextHolder.getContext().authentication
        Assertions.assertNull(authentication)
    }
}