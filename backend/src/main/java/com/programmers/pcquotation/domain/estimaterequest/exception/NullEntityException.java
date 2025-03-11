package com.programmers.pcquotation.domain.estimaterequest.exception;

public class NullEntityException extends RuntimeException {
    public NullEntityException() {
        super();
    }

    public NullEntityException(String message) {
        super(message);
    }
}
