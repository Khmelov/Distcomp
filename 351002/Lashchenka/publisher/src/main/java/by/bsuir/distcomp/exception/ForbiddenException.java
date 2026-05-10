package by.bsuir.distcomp.exception;

public class ForbiddenException extends RuntimeException {
    private final int errorCode;

    public ForbiddenException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
