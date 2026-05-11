package com.adashkevich.rest.lab.exception;

public class UnauthorizedException extends ApiException {
    public UnauthorizedException(String message, String errorCode) {
        super(message, 401, errorCode);
    }
}
