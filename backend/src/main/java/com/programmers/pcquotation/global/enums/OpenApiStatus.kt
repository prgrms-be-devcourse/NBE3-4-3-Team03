package com.programmers.pcquotation.global.enums;


enum class OpenApiStatus(val value: String) {
    RUN("계속사업자"),
    SUSPENSION("휴업자"),
    CLOSURE("폐업자"),
    NONE("")
}
