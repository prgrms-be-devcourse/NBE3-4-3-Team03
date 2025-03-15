package com.programmers.pcquotation

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication(scanBasePackages = ["com.programmers.pcquotation"])
@EnableJpaAuditing
@ConfigurationPropertiesScan("com.programmers.pcquotation.global.properties")
class PcquotationApplication

fun main(args: Array<String>) {
    SpringApplication.run(PcquotationApplication::class.java, *args)
}