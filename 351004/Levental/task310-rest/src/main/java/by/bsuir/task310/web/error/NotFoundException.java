package by.bsuir.task310.web.error;

public class NotFoundException extends ApiException {
    public NotFoundException(String message, String codeSuffix) {
        super(message, 404, codeSuffix);
    }
}
