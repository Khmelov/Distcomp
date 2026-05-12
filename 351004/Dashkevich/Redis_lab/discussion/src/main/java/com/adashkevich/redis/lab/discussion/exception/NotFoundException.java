package com.adashkevich.redis.lab.discussion.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ApiException {
    public NotFoundException(String message, String code) {
        super(message, code, HttpStatus.NOT_FOUND);
    }
}
