package com.distcomp.exception;

import org.springframework.http.HttpStatus;

/**
 * Raised when an entity with the requested identifier does not exist.
 */
public class EntityNotFoundException extends ApiException {

    public EntityNotFoundException(String entityName, Long id) {
        super(HttpStatus.NOT_FOUND, "40401", entityName + " with id " + id + " not found");
    }

    public EntityNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "40402", message);
    }
}
