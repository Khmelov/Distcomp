package by.bsuir.distcomp.exception;

public class ResourceNotFoundException extends RuntimeException {
    private final String errorCode;

    public ResourceNotFoundException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ResourceNotFoundException(String message, int errorCode) {
        super(message);
        this.errorCode = String.valueOf(errorCode);
    }

    public String getErrorCode() {
        return errorCode;
    }
}
