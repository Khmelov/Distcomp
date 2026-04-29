package by.bsuir.distcomp.exception;

public class ResourceNotFoundException extends RuntimeException {
    private final int errorCode;

    public ResourceNotFoundException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
