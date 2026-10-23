package com.distcomp.exception;

import org.springframework.http.HttpStatus;

/**
 * Raised when a request payload violates the domain constraints.
 */
public class ValidationException extends ApiException {

    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    public ValidationException(String message) {
        super(STATUS, "40001", message);
    }

    public ValidationException(String field, String message) {
        super(STATUS, "40001", "Field '" + field + "' " + message);
    }
}
