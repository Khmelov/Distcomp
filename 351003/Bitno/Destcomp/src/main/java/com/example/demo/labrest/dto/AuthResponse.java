package com.example.demo.labrest.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponse {
    private String access_token;
    private String token_type = "Bearer";
    private Long expires_in;
    private String role;
}