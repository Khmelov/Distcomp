package by.bsuir.task310.web.error;

public class ValidationException extends ApiException {
    public ValidationException(String message, String codeSuffix) {
        super(message, 400, codeSuffix);
    }
}
