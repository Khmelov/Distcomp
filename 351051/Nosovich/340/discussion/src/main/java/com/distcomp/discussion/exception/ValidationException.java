package com.distcomp.discussion.exception;

import org.springframework.http.HttpStatus;

/**
 * Raised when the request payload violates the notice constraints.
 */
public class ValidationException extends ApiException {

    public ValidationException(String message) {
        super(HttpStatus.BAD_REQUEST, "40001", message);
    }

    public ValidationException(String field, String message) {
        super(HttpStatus.BAD_REQUEST, "40001", "Field '" + field + "' " + message);
    }
}
