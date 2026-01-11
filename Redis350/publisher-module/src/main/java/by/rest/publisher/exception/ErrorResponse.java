package by.rest.publisher.exception;

public class ErrorResponse {
    private String errorMessage;
    private String errorCode;
    private String timestamp;
    
    public ErrorResponse() {
        this.timestamp = java.time.LocalDateTime.now().toString();
    }
    
    public ErrorResponse(String errorMessage, String errorCode) {
        this();
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }
    
    // Геттеры и сеттеры
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}