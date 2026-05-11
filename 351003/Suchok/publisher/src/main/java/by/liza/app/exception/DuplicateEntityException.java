package by.liza.app.exception;

public class DuplicateEntityException extends RuntimeException {

    private final int errorCode;

    public DuplicateEntityException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}