package com.example.task320jpa.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для входящих запросов на создание/обновление Mark
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarkRequestTo {
    
    /**
     * ID метки (используется только для UPDATE операций)
     */
    @JsonProperty("id")
    private Long id;
    
    /**
     * Название метки
     */
    @JsonProperty("name")
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 32, message = "Name must be between 2 and 32 characters")
    private String name;
}
