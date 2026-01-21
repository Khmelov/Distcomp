// NotFoundException.java
package com.example.publisher.exception;

public class NotFoundException extends RuntimeException {
    private final int errorCode;

    public NotFoundException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}