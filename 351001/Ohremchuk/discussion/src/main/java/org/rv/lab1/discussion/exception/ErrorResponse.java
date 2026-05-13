package org.rv.lab1.discussion.exception;

public record ErrorResponse(
        String errorMessage,
        String errorCode
) {
}

