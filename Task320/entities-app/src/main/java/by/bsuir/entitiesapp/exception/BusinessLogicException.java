package by.bsuir.entitiesapp.exception;

public class BusinessLogicException extends RuntimeException {
    public final String errorCode;

    public BusinessLogicException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}