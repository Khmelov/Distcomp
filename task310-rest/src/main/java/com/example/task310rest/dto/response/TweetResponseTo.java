package com.example.task310rest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для исходящих ответов Tweet
 * Возвращается клиенту в JSON формате с ключом "tweet"
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TweetResponseTo {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("userId")
    private Long userId;
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("content")
    private String content;
}
