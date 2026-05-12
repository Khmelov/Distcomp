package com.adashkevich.redis.lab.exception;

public abstract class ApiException extends RuntimeException {
    public final int httpStatus;
    public final String errorCode;

    protected ApiException(String message, int httpStatus, String errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }
}
