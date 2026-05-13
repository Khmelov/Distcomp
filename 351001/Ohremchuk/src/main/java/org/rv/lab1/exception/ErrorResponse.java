package org.rv.lab1.exception;

public record ErrorResponse(
        String errorMessage,
        String errorCode
) {
}

