package com.programmers.pcquotation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = "com.programmers.pcquotation")
@EnableJpaAuditing
public class PcquotationApplication {
    public static void main(String[] args) {
        SpringApplication.run(PcquotationApplication.class, args);
    }
}