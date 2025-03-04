package com.bsuir.dc.util.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {
    private int code;
    private HttpStatus status;

    public CustomException(String msg, int code, HttpStatus status) {
        super(msg);
        this.code = code;
        this.status = status;
    }

    public int getErrorCode() {
        return code;
    }

    public HttpStatus getHttpStatus() {
        return status;
    }
}
