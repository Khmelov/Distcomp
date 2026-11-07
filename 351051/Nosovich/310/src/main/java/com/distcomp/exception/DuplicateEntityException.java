package com.distcomp.exception;

import org.springframework.http.HttpStatus;

/**
 * Raised when a unique domain value is reused, for example a creator login.
 */
public class DuplicateEntityException extends ApiException {

    private static final HttpStatus STATUS = HttpStatus.CONFLICT;

    public DuplicateEntityException(String entityName, String field, Object value) {
        super(STATUS, "40901",
                entityName + " with " + field + " '" + value + "' already exists");
    }
}
