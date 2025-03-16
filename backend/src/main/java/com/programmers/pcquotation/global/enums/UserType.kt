package com.programmers.pcquotation.global.enums;

enum class UserType(val value: String) {
    CUSTOMER("CUSTOMER"),
    SELLER("SELLER"),
    ADMIN("ADMIN"),
    NOTHING("NOTHING");

    companion object {
        fun fromString(value: String?): UserType {
            return when (value) {
                "[ROLE_CUSTOMER]" -> CUSTOMER
                "[ROLE_SELLER]" -> SELLER
                "[ROLE_ADMIN]" -> ADMIN
                null -> NOTHING
                else -> entries.find { it.name.equals(value, ignoreCase = true) } ?: NOTHING
            }
        }
    }
}