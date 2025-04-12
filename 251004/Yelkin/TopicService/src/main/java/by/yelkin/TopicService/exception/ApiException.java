package by.yelkin.TopicService.exception;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.time.LocalDateTime;

@Getter
public class ApiException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -6851943050721751285L;

    /**
     * Дата и время ошибки
     */
    private final LocalDateTime timestamp;

    /**
     * Код ошибки по справочнику
     */
    private final String code;

    /**
     * Наименование ошибки по справочнику
     */
    private final String name;

    /**
     * HTTP статус
     */
    private final HttpStatus httpStatus;

    /**
     * Сообщение для пользователя
     */
    private final String displayMessage;

    public ApiException(@NonNull ApiError error) {
        super(error.name());
        this.timestamp = LocalDateTime.now();
        this.code = error.getCode();
        this.name = error.name();
        this.displayMessage = error.getDisplayMessage();
        this.httpStatus = error.getHttpStatus();
    }

    public ApiException(@NonNull ApiError error, @NonNull String displayMessage) {
        super(error.name());
        this.timestamp = LocalDateTime.now();
        this.code = error.getCode();
        this.name = error.name();
        this.displayMessage = String.format(error.getDisplayMessage(), displayMessage);
        this.httpStatus = error.getHttpStatus();
    }

}
