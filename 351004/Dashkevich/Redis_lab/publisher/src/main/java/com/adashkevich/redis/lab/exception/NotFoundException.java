package com.adashkevich.redis.lab.exception;

public class NotFoundException extends ApiException {
    public NotFoundException(String message, String errorCode) {
        super(message, 404, errorCode);
    }
}
