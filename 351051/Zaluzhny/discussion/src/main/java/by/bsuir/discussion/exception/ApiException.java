package by.bsuir.discussion.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final int errorCode;

    public ApiException(HttpStatus status, ErrorCode errorCode, String message) {
        super(message);
        if (!Integer.toString(errorCode.getCode()).startsWith(Integer.toString(status.value()))) {
            throw new IllegalStateException("errorCode must start with the HTTP status");
        }
        this.status = status;
        this.errorCode = errorCode.getCode();
    }

    public static ApiException notFound(Long id) {
        return new ApiException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND, "Message with id " + id + " not found");
    }

    public HttpStatus getStatus() {
        return status;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
