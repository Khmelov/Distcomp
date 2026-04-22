package by.tracker.rest_api.exception;

public class ResourceNotFoundException extends RuntimeException {
    private final Integer errorCode;

    public ResourceNotFoundException(String message, Integer errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() { return errorCode; }
}