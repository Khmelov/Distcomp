package com.example.news.exception;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {
    private final String customCode;

    public EntityNotFoundException(String message, String customCode) {
        super(message);
        this.customCode = customCode;
    }
}