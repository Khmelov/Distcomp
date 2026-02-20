package com.lizaveta.notebook.exception;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends RuntimeException {

    private static final int ERROR_CODE = 40401;

    public ResourceNotFoundException(final String message) {
        super(message);
    }

    public int getErrorCode() {
        return ERROR_CODE;
    }
}
