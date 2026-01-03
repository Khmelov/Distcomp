package com.task.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorRequestTo {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("login")
    @NotBlank(message = "Login is required")
    @Size(min = 2, max = 64, message = "Login must be between 2 and 64 characters")
    private String login;

    @JsonProperty("password")
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    private String password;

    @JsonProperty("firstname")
    @NotBlank(message = "Firstname is required")
    @Size(min = 2, max = 64, message = "Firstname must be between 2 and 64 characters")
    private String firstname;

    @JsonProperty("lastname")
    @NotBlank(message = "Lastname is required")
    @Size(min = 2, max = 64, message = "Lastname must be between 2 and 64 characters")
    private String lastname;
}