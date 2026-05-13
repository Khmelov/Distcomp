package by.bsuir.task310.web.error;

public abstract class ApiException extends RuntimeException {
    private final int httpStatus;
    private final String codeSuffix;

    protected ApiException(String message, int httpStatus, String codeSuffix) {
        super(message);
        this.httpStatus = httpStatus;
        this.codeSuffix = codeSuffix;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getCodeSuffix() {
        return codeSuffix;
    }
}
