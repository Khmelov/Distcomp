package by.tracker.rest_api.exception;

public class ValidationException extends RuntimeException {
    private final Integer errorCode;

    public ValidationException(String message, Integer errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() { return errorCode; }
}