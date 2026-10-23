package com.distcomp.exception;

import org.springframework.http.HttpStatus;

/**
 * Raised when an entity with the requested identifier does not exist.
 */
public class EntityNotFoundException extends ApiException {

    private static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

    public EntityNotFoundException(String entityName, Long id) {
        super(STATUS, "40401", entityName + " with id " + id + " not found");
    }

    public EntityNotFoundException(String message) {
        super(STATUS, "40402", message);
    }
}
