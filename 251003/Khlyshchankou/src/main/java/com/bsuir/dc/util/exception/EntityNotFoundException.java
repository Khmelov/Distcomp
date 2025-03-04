package com.bsuir.dc.util.exception;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends CustomException {
    public EntityNotFoundException(String message) {
        super(message, 40401, HttpStatus.NOT_FOUND);
    }
}
