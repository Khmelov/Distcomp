package com.lizaveta.notebook.exception;

public class ValidationException extends RuntimeException {

    private final int errorCode;

    public ValidationException(final String message, final int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
