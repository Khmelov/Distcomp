package com.example.task320jpa.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для входящих запросов на создание/обновление Note
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteRequestTo {
    
    /**
     * ID заметки (используется только для UPDATE операций)
     */
    @JsonProperty("id")
    private Long id;
    
    /**
     * ID твита, к которому относится заметка
     */
    @JsonProperty("tweetId")
    @NotNull(message = "Tweet ID cannot be null")
    private Long tweetId;
    
    /**
     * Содержание заметки
     */
    @JsonProperty("content")
    @NotBlank(message = "Content cannot be blank")
    @Size(min = 2, max = 2048, message = "Content must be between 2 and 2048 characters")
    private String content;
}
