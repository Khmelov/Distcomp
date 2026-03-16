package com.example.restapp.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    ERR_ENTITY_NOT_FOUND("Entity not found", 40401),
    ERR_VALIDATION_FAILED("Validation failed", 40001),
    ERR_INTERNAL_SERVER("Internal server error", 50001);

    private final String message;
    private final int code;
}