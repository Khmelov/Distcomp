package com.distcomp.discussion.exception;

import org.springframework.http.HttpStatus;

/**
 * Base class of every error returned by the discussion module.
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    private final String code;

    public ApiException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }
}
