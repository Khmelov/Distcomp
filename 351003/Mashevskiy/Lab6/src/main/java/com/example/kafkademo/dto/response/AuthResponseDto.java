package com.example.kafkademo.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    private String access_token;
    private String token_type = "Bearer";

    public AuthResponseDto(String access_token) {
        this.access_token = access_token;
        this.token_type = "Bearer";
    }
}