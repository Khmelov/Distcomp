package com.bsuir.dc.dto.response;

import java.util.Date;

public class ErrorResponse {
    private int code;
    private String msg;
    private Date timestamp;

    public ErrorResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.timestamp = new Date();
    }

    public int getErrorCode() { return code; }
    public String getErrorMessage() { return msg; }
    public Date getTimestamp() { return timestamp; }
}
