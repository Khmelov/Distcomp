package by.molchan.Publisher.utils.exceptions;

import org.springframework.http.HttpStatus;

public class CustomInformativeException extends RuntimeException {
    private int errorCode;
    private HttpStatus httpStatus;

    public CustomInformativeException(String message, int errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
