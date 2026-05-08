package com.example.task310.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestTo {
    @Size(min = 2, max = 64)
    private String login;
    @Size(min = 8, max = 128)
    private String password;
}
