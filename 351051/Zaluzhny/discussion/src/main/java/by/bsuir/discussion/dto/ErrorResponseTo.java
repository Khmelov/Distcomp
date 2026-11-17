package by.bsuir.discussion.dto;

public class ErrorResponseTo {

    private final String errorMessage;
    private final Integer errorCode;

    public ErrorResponseTo(String errorMessage, Integer errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Integer getErrorCode() {
        return errorCode;
    }
}
