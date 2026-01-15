package com.example.task320jpa.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для исходящих ответов User
 * Возвращается клиенту в JSON формате с ключом "user"
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseTo {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("login")
    private String login;
    
    @JsonProperty("firstname")
    private String firstname;
    
    @JsonProperty("lastname")
    private String lastname;
}
