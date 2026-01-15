package com.example.task320jpa.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для ошибок
 * Возвращается клиенту при возникновении ошибок валидации или бизнес-логики
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    /**
     * Код ошибки (HTTP статус код с пользовательским префиксом)
     * Формат: 4xx для ошибок клиента, где первые 3 цифры совпадают с HTTP кодом
     */
    @JsonProperty("errorCode")
    private String errorCode;
    
    /**
     * Сообщение об ошибке
     */
    @JsonProperty("errorMessage")
    private String errorMessage;
}
