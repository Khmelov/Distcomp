package com.example.Labs.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class JwtResponse {
    private String access_token;
}