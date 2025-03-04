package com.programmers.pcquotation.domain.customer.dto;

import com.programmers.pcquotation.domain.customer.entity.Customer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomerSignupRequest {
    private String username;
    private String password;
    private String confirmPassword;
    private String customerName;
    private String email;
    private String verificationQuestion;
    private String verificationAnswer;

    public Customer toCustomer() {
        return Customer.builder()
                .username(username)
                .password(password)
                .customerName(customerName)
                .email(email)
                .verificationQuestion(verificationQuestion)
                .verificationAnswer(verificationAnswer)
                .build();
    }
}