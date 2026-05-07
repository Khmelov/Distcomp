package by.bsuir.distcomp.exception;

public class ValidationException extends RuntimeException {
    private final String errorCode;

    public ValidationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ValidationException(String message, int errorCode) {
        super(message);
        this.errorCode = String.valueOf(errorCode);
    }

    public String getErrorCode() {
        return errorCode;
    }
}
