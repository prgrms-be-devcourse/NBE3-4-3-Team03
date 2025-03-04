package com.programmers.pcquotation.domain.customer.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.programmers.pcquotation.domain.customer.entity.Customer;
import com.programmers.pcquotation.domain.customer.repository.CustomerRepository;
import com.programmers.pcquotation.domain.member.entitiy.Member;
import com.programmers.pcquotation.domain.seller.entitiy.Seller;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public void createCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    public Optional<Customer> findCustomerByUsername(String username) {
        return customerRepository.getCustomerByUsername(username);
    }

    public Optional<Customer> findCustomerByEmail(String email) {
        return customerRepository.getCustomerByEmail(email);
    }

    public Optional<Member> findByApiKey(String apiKey) {
        return customerRepository.findByApiKey(apiKey).map(customer -> customer);
    }

    public Optional<Member> findById(Long id) {
        return customerRepository.findById(id).map(customer -> customer);
    }

}