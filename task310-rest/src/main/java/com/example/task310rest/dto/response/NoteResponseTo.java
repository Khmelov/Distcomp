package com.example.task310rest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для исходящих ответов Note
 * Возвращается клиенту в JSON формате с ключом "note"
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteResponseTo {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("tweetId")
    private Long tweetId;
    
    @JsonProperty("content")
    private String content;
}
