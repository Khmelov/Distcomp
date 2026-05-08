package com.sergey.orsik.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestTo {

    @NotBlank(message = "login must not be blank")
    private String login;

    @NotBlank(message = "password must not be blank")
    private String password;
}
