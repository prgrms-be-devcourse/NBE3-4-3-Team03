package com.programmers.pcquotation.member.service;


import com.programmers.pcquotation.domain.customer.dto.CustomerSignupRequest;
import com.programmers.pcquotation.domain.customer.entity.Customer;
import com.programmers.pcquotation.domain.customer.exception.CustomerAlreadyExistException;
import com.programmers.pcquotation.domain.customer.exception.IncorrectLoginAttemptException;
import com.programmers.pcquotation.domain.customer.exception.PasswordMismatchException;
import com.programmers.pcquotation.domain.customer.repository.CustomerRepository;
import com.programmers.pcquotation.domain.member.dto.LoginRequest;
import com.programmers.pcquotation.domain.member.service.AuthService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
@ActiveProfiles("test")
@SpringBootTest
public class AuthServiceTest {
    @Autowired
    private AuthService authService;

    @MockitoBean
    private CustomerRepository customerRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;




    @Test
    public void signup_passwordMismatch() {
        CustomerSignupRequest customerSignupRequest = new CustomerSignupRequest(
                "user1",
                "1234",
                "1111",
                "홍길동",
                "test@test.com",
                "가장 좋아하는 음식은",
                "밥"
        );

        assertThrows(PasswordMismatchException.class, () -> authService.processSignup(customerSignupRequest));
    }

    @Test
    public void signup_customerUserNameAlreadyExist() {
        CustomerSignupRequest customerSignupRequest = new CustomerSignupRequest(
                "user1",
                "1234",
                "1234",
                "홍길동",
                "test@test.com",
                "가장 좋아하는 음식은",
                "밥"
        );

        Customer customer = customerSignupRequest.toCustomer();
        when(customerRepository.getCustomerByUsername(customerSignupRequest.getUsername())).thenReturn(Optional.of(customer));

        assertThrows(CustomerAlreadyExistException.class, () -> authService.processSignup(customerSignupRequest));
    }

    @Test
    public void signup_customerEmailAlreadyExist() {
        CustomerSignupRequest customerSignupRequest = new CustomerSignupRequest(
                "user1",
                "1234",
                "1234",
                "홍길동",
                "test@test.com",
                "가장 좋아하는 음식은",
                "밥"
        );

        Customer customer = customerSignupRequest.toCustomer();
        when(customerRepository.getCustomerByEmail(customerSignupRequest.getEmail())).thenReturn(Optional.of(customer));

        assertThrows(CustomerAlreadyExistException.class, () -> authService.processSignup(customerSignupRequest));
    }



    @Test
    public void login_usernameNotFound() {
        LoginRequest customerLoginRequest = new LoginRequest(
                "user1",
                "1234"
        );

        when(customerRepository.getCustomerByUsername("user1")).thenReturn(Optional.empty());

        assertThrows(IncorrectLoginAttemptException.class, () -> {
            authService.processLoginCustomer(customerLoginRequest);
        });

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
    }

    @Test
    public void login_incorrectPassword() {
        LoginRequest customerLoginRequest = new LoginRequest(
                "user1",
                "1234"
        );

        Customer customer = Customer.builder()
                .username("user1")
                .password("1111")
                .build();

        when(customerRepository.getCustomerByUsername("user1")).thenReturn(Optional.of(customer));

        assertThrows(IncorrectLoginAttemptException.class, () -> {
            authService.processLoginCustomer(customerLoginRequest);
        });

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
    }
}