package by.bsuir.rest.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final int errorCode;

    public ApiException(HttpStatus status, ErrorCode errorCode, String message) {
        super(message);
        if (!Integer.toString(errorCode.getCode()).startsWith(Integer.toString(status.value()))) {
            throw new IllegalStateException("errorCode must start with the HTTP status");
        }
        this.status = status;
        this.errorCode = errorCode.getCode();
    }

    public static ApiException notFound(String entityName, Long id) {
        return new ApiException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND,
                entityName + " with id " + id + " not found");
    }
}
