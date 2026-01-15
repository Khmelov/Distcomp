package com.example.task310rest.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для входящих запросов на создание/обновление Tweet
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TweetRequestTo {
    
    /**
     * ID твита (используется только для UPDATE операций)
     */
    @JsonProperty("id")
    private Long id;
    
    /**
     * ID пользователя, создавшего твит
     */
    @JsonProperty("userId")
    @NotNull(message = "User ID cannot be null")
    private Long userId;
    
    /**
     * Заголовок твита
     */
    @JsonProperty("title")
    @NotBlank(message = "Title cannot be blank")
    @Size(min = 2, max = 64, message = "Title must be between 2 and 64 characters")
    private String title;
    
    /**
     * Содержание твита
     */
    @JsonProperty("content")
    @NotBlank(message = "Content cannot be blank")
    @Size(min = 4, max = 2048, message = "Content must be between 4 and 2048 characters")
    private String content;
}
