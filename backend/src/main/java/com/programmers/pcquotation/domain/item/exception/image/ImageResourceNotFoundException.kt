package com.programmers.pcquotation.domain.item.exception.image

/**
 * 요청한 리소스가 존재하지 않는 경우에 발생하는 예외
 */
class ImageResourceNotFoundException(message: String) : RuntimeException(message)