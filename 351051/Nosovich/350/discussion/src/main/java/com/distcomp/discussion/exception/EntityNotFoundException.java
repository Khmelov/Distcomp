package com.distcomp.discussion.exception;

import org.springframework.http.HttpStatus;

/**
 * Raised when a notice with the requested identifier is absent.
 */
public class EntityNotFoundException extends ApiException {

    public EntityNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND, "40401", "Notice with id " + id + " not found");
    }
}
