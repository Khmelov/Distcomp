package com.example.task320jpa.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для исходящих ответов Mark
 * Возвращается клиенту в JSON формате с ключом "mark"
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarkResponseTo {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("name")
    private String name;
}
