package com.example.lab.exception;

public class EntityNotFoundException extends RuntimeException {
    private final int errorCode;

    public EntityNotFoundException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() { return errorCode; }
}