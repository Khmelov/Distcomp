package by.bsuir.task330.discussion.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ApiException {
    public NotFoundException(String message, int suffix) {
        super(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value() * 100 + suffix, message);
    }
}
