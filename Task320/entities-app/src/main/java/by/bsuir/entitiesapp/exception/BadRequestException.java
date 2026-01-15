package by.bsuir.entitiesapp.exception;

public class BadRequestException extends RuntimeException {
    public final String errorCode;

    public BadRequestException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
