package by.molchan.Publisher.DTOs.Responses;

import java.util.Date;

public class ErrorResponse {
    private int errorCode;
    private String errorMessage;
    private Date timestamp;

    public ErrorResponse(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.timestamp = new Date();
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
