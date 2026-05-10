package by.tracker.rest_api.exception;

public class DuplicateResourceException extends RuntimeException {
    private final Integer errorCode;

    public DuplicateResourceException(String message, Integer errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() { return errorCode; }
}