package org.rv.lab1.discussion.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final int suffix;

    public ApiException(HttpStatus status, int suffix, String message) {
        super(message);
        this.status = status;
        this.suffix = suffix;
    }

    public HttpStatus status() {
        return status;
    }

    public String errorCode() {
        int code = status.value() * 100 + Math.max(0, Math.min(99, suffix));
        return String.format("%05d", code);
    }
}

