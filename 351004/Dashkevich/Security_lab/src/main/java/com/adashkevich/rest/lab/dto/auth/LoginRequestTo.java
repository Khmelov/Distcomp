package com.adashkevich.rest.lab.dto.auth;

import jakarta.validation.constraints.NotBlank;

public class LoginRequestTo {
    @NotBlank
    public String login;

    @NotBlank
    public String password;
}
