package com.adashkevich.jpa.lab.exception;

public class ConflictException extends ApiException {
    public ConflictException(String message, String errorCode) {
        super(message, 409, errorCode);
    }
}
