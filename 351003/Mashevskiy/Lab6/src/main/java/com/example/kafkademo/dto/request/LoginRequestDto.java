package com.example.kafkademo.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class LoginRequestDto {
    @NotBlank
    private String login;

    @NotBlank
    private String password;
}