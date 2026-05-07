package by.bsuir.distcomp.exception;

public class DuplicateException extends RuntimeException {
    private final String errorCode;

    public DuplicateException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public DuplicateException(String message, int errorCode) {
        super(message);
        this.errorCode = String.valueOf(errorCode);
    }

    public String getErrorCode() {
        return errorCode;
    }
}
