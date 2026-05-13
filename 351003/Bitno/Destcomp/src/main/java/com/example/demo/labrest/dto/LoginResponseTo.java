package com.example.demo.labrest.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseTo {
    private String access_token;
    private String token_type = "Bearer";
    public LoginResponseTo(String token) {
        this.access_token = token;
        this.token_type = "Bearer";
    }
}