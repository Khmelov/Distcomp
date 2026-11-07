package com.distcomp.exception;

import org.springframework.http.HttpStatus;

/**
 * Raised when a value that must be unique is already used by another entity.
 *
 * <p>The API contract prescribes {@code 403} for this situation (not
 * {@code 409}), therefore the status is fixed here.</p>
 */
public class DuplicateEntityException extends ApiException {

    public DuplicateEntityException(String entityName, String field, Object value) {
        super(HttpStatus.FORBIDDEN, "40301",
                entityName + " with " + field + " '" + value + "' already exists");
    }
}
