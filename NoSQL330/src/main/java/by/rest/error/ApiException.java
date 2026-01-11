// src/main/java/by/rest/error/ApiException.java
package by.rest.error;

public class ApiException extends RuntimeException {
    private final int httpStatus;
    private final String errorCode;

    public ApiException(int httpStatus, String errorCode, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }
    public int getHttpStatus() { return httpStatus; }
    public String getErrorCode() { return errorCode; }
}
