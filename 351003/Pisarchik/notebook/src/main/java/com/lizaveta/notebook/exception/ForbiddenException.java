package com.lizaveta.notebook.exception;

public class ForbiddenException extends RuntimeException {

    private static final int ERROR_CODE = 40301;

    public ForbiddenException(final String message) {
        super(message);
    }

    public int getErrorCode() {
        return ERROR_CODE;
    }
}
