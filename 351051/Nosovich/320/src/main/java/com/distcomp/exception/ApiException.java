package com.distcomp.exception;

import org.springframework.http.HttpStatus;

/**
 * Base class for every error reported to an API client.
 *
 * <p>Carries the HTTP status and the five-digit application code required by
 * the specification: the first three digits repeat the HTTP status, the last
 * two identify the concrete problem.</p>
 */
public class ApiException extends RuntimeException {

    private final HttpStatus httpStatus;

    private final String errorCode;

    protected ApiException(HttpStatus httpStatus, String errorCode, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
