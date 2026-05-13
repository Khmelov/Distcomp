package com.example.demo.labrest.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestTo {
    @NotBlank(message = "Login is required")
    private String login;

    @NotBlank(message = "Password is required")
    private String password;
}