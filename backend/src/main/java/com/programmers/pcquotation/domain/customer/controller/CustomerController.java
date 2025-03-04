package com.programmers.pcquotation.domain.customer.controller;

import com.programmers.pcquotation.domain.customer.dto.CustomerInfoResponse;
import com.programmers.pcquotation.domain.customer.entity.Customer;
import com.programmers.pcquotation.domain.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RequiredArgsConstructor
@RestController
@RequestMapping(("/customer"))
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping
    @Transactional(readOnly = true)
    public CustomerInfoResponse info(Principal principal) {
        Customer customer = customerService
                .findCustomerByUsername(principal.getName())
                .orElseThrow(() -> new NullPointerException("존재하지 않는 사용자입니다."));

        return CustomerInfoResponse.builder()
                .id(customer.getId())
                .username(customer.getUsername())
                .customerName(customer.getCustomerName())
                .email(customer.getEmail()).build();
    }
}