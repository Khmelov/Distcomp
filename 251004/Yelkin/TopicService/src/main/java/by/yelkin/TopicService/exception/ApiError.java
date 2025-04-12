package by.yelkin.TopicService.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiError {
    ERR_BAD_FORMAT("ERR-400:01", "Недопустимый формат: [%s]", HttpStatus.BAD_REQUEST),
    ERR_CREATOR_NOT_FOUND("ERR-404:01", "Creator with id - %s not found", HttpStatus.NOT_FOUND),
    ERR_TOPIC_NOT_FOUND("ERR-404:02", "Topic with id - %s not found", HttpStatus.NOT_FOUND),
    ERR_MARK_NOT_FOUND("ERR-404:03", "Mark with id - %s not found", HttpStatus.NOT_FOUND),
    ERR_COMMENT_NOT_FOUND("ERR-404:01", "Comment with id - %s not found", HttpStatus.NOT_FOUND),

    ERR_INTERNAL_ERROR("ERR-500:0000", "Техническая ошибка", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String displayMessage;
    private final HttpStatus httpStatus;

    ApiError(String code, String displayMessage, HttpStatus httpStatus) {
        this.code = code;
        this.displayMessage = displayMessage;
        this.httpStatus = httpStatus;
    }

}