package by.bsuir.task310.web.error;

public class ConflictException extends ApiException {
    public ConflictException(String message, String codeSuffix) {
        super(message, 409, codeSuffix);
    }
}
