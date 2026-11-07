package com.distcomp.exception;

import org.springframework.http.HttpStatus;

/**
 * Raised when a request payload violates the domain constraints.
 */
public class ValidationException extends ApiException {

    public ValidationException(String message) {
        super(HttpStatus.BAD_REQUEST, "40001", message);
    }

    public ValidationException(String field, String message) {
        super(HttpStatus.BAD_REQUEST, "40001", "Field '" + field + "' " + message);
    }
}
