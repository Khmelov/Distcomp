package com.aitor.publisher.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EmptyIdException extends ServiceException {
    public EmptyIdException() {
        super("Request with necessary ID does not include ID");
        this.statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
