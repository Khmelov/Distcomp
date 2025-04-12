package by.molchan.Publisher.DTOs.Responses;

import java.util.Date;
import java.util.Map;

public class ValidationFailedResponse {
    private int errorCode;
    private Map<String, String> errors;
    private Date timestamp;

    public ValidationFailedResponse(int errorCode, Map<String, String> errors) {
        this.errorCode = errorCode;
        this.errors = errors;
        this.timestamp = new Date();

    }

    public int getErrorCode() {
        return errorCode;
    }


    public Date getTimestamp() {
        return timestamp;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
