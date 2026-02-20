package by.bsuir.distcomp.exception;

public class DuplicateException extends RuntimeException {
    private final int errorCode;

    public DuplicateException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
