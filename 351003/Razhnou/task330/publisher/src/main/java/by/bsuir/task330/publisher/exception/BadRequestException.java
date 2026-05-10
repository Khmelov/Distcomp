package by.bsuir.task330.publisher.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ApiException {
    public BadRequestException(String message, int suffix) {
        super(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.value() * 100 + suffix, message);
    }
}
