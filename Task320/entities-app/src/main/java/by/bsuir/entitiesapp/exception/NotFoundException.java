package by.bsuir.entitiesapp.exception;

public class NotFoundException extends RuntimeException {
    public final String errorCode;

    public NotFoundException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
