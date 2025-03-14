package com.programmers.pcquotation.domain.estimaterequest.exception

class NullEntityException : RuntimeException {
    constructor() : super()

    constructor(message: String) : super(message)
}