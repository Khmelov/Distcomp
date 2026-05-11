package by.bsuir.task340.publisher.error;


public class ApiErrorResponse {
    private String errorMessage;
    private Integer errorCode;

    public ApiErrorResponse() {
    }

    public ApiErrorResponse(String errorMessage, Integer errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }
}
