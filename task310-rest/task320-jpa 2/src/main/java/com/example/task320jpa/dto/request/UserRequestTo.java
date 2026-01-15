package com.example.task320jpa.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для входящих запросов на создание/обновление User
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestTo {
    
    /**
     * ID пользователя (используется только для UPDATE операций)
     */
    @JsonProperty("id")
    private Long id;
    
    /**
     * Логин пользователя (email)
     */
    @JsonProperty("login")
    @NotBlank(message = "Login cannot be blank")
    @Size(min = 2, max = 64, message = "Login must be between 2 and 64 characters")
    private String login;
    
    /**
     * Пароль пользователя
     */
    @JsonProperty("password")
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    private String password;
    
    /**
     * Имя пользователя
     */
    @JsonProperty("firstname")
    @NotBlank(message = "Firstname cannot be blank")
    @Size(min = 2, max = 64, message = "Firstname must be between 2 and 64 characters")
    private String firstname;
    
    /**
     * Фамилия пользователя
     */
    @JsonProperty("lastname")
    @NotBlank(message = "Lastname cannot be blank")
    @Size(min = 2, max = 64, message = "Lastname must be between 2 and 64 characters")
    private String lastname;
}
