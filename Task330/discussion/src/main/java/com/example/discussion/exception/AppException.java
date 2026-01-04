package com.example.discussion.exception;

public class AppException extends RuntimeException {
    private final int errorCode;

    public AppException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() { return errorCode; }
}