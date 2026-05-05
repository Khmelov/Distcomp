package by.bsuir.task361.discussion.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends ApiException {
    public ConflictException(String message, int suffix) {
        super(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.value() * 100 + suffix, message);
    }
}
