package com.adashkevich.rest.lab.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class EditorRequestTo {
    @NotBlank @Size(min = 2, max = 64)
    public String login;

    @NotBlank @Size(min = 8, max = 128)
    public String password;

    @NotBlank @Size(min = 2, max = 64)
    public String firstname;

    @NotBlank @Size(min = 2, max = 64)
    public String lastname;
}
