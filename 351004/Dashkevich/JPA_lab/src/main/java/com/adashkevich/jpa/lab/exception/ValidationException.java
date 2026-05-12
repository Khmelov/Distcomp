package com.adashkevich.jpa.lab.exception;

public class ValidationException extends ApiException {
    public ValidationException(String message, String errorCode) {
        super(message, 400, errorCode);
    }
}
