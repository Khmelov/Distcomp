package com.bsuir.dc.dto.response;

import java.util.Date;
import java.util.Map;

public class ValidationFailedResponse {
    private int code;
    private Map<String, String> errors;
    private Date timestamp;

    public ValidationFailedResponse(int code, Map<String, String> errors) {
        this.code = code;
        this.errors = errors;
        this.timestamp = new Date();
    }

    public int getErrorCode() { return code; }
    public Date getTimestamp() { return timestamp; }
    public Map<String, String> getErrors() { return errors; }
}
