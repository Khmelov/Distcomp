package com.example.rest.dto.requestDto;

import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class UserRequestTo {

    @Size(min = 2, max = 64)
    private String login;

    @Size(min = 8, max = 128)
    private String password;

    @Size(min = 2, max = 64)
    private String firstname;

    @Size(min = 2, max = 64)
    private String lastname;
}
