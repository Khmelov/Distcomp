package com.example.demo.labrest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class AuthRequest {
    @NotBlank private String login;
    @NotBlank private String password;
}