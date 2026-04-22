package com.example.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {
    private final String customCode;

    public EntityNotFoundException(String message, String customCode) {
        super(message);
        this.customCode = customCode;
    }
}