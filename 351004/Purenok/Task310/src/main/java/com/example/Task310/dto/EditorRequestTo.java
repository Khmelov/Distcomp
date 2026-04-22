package com.example.Task310.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditorRequestTo {
    private Long id;

    @NotBlank(message = "Login is required")
    @Size(min = 2, max = 64)
    private String login;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128)
    private String password;

    @NotBlank(message = "Firstname is required")
    @Size(min = 2, max = 64)
    private String firstname;

    @NotBlank(message = "Lastname is required")
    @Size(min = 2, max = 64)
    private String lastname;
}