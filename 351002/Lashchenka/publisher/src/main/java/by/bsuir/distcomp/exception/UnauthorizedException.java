package by.bsuir.distcomp.exception;

public class UnauthorizedException extends RuntimeException {
    private final int errorCode;

    public UnauthorizedException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
