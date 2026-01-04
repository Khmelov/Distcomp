package com.aitor.publisher.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class ServiceException extends RuntimeException {
    protected HttpStatusCode statusCode;
    public ServiceException(String message) {
        super(message);
    }
}
