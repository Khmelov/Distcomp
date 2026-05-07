package com.lizaveta.notebook.exception;

public class ResourceNotFoundException extends RuntimeException {

    private static final int ERROR_CODE = 40401;

    public ResourceNotFoundException(final String message) {
        super(message);
    }

    public int getErrorCode() {
        return ERROR_CODE;
    }
}
