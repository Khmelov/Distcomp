package com.lizaveta.notebook.exception;

public class UnauthorizedException extends RuntimeException {

    private static final int DEFAULT_ERROR_CODE = 40101;
    private final int errorCode;

    public UnauthorizedException(final String message) {
        super(message);
        this.errorCode = DEFAULT_ERROR_CODE;
    }

    public UnauthorizedException(final String message, final int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
