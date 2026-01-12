package com.task.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class DuplicateException extends RuntimeException {
    public DuplicateException(String message) {
        super(message);
    }
}