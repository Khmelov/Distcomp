package by.bsuir.task330.publisher.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final int errorCode;

    public ApiException(HttpStatus status, int errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
