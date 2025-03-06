package com.programmers.pcquotation.domain.member.dto

import com.fasterxml.jackson.annotation.JsonInclude

class ErrorResponse(
    var message: String
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var errorCode: String? = null
}