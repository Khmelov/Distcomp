package by.bsuir.distcomp.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse {
    private final String errorMessage;
    private final String errorCode;

    public ErrorResponse(String errorMessage, HttpStatus status, int customCode) {
        this.errorMessage = errorMessage;
        this.errorCode = generateErrorCode(status, customCode);
    }

    private String generateErrorCode(HttpStatus status, int customCode) {
        return String.format("%d%02d", status.value(), customCode);
    }
}
