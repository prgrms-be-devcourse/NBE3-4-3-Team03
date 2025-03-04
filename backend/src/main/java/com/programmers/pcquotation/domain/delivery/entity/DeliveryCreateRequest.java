package com.programmers.pcquotation.domain.delivery.entity;

import jakarta.validation.constraints.NotBlank;

public record DeliveryCreateRequest(@NotBlank String address){}

