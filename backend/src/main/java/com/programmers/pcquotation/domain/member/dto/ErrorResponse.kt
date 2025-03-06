package com.programmers.pcquotation.domain.member.dto

import com.fasterxml.jackson.annotation.JsonInclude
import lombok.Getter

class ErrorResponse(
    var message: String
){
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var errorCode: String? = null
}