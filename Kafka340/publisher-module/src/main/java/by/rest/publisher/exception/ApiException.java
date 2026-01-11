package by.rest.publisher.exception;

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
    
    @Override
    public String toString() {
        return String.format("ApiException{httpStatus=%d, errorCode='%s', message='%s'}", 
                httpStatus, errorCode, getMessage());
    }
}