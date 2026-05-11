package com.adashkevich.jpa.lab.exception;

public class ForbiddenException extends ApiException {
    public ForbiddenException(String message, String errorCode) {
        super(message, 403, errorCode);
    }
}