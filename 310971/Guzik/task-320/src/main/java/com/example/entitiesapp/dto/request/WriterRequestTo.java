package com.example.entitiesapp.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WriterRequestTo {
    @Size(min = 2, max = 64, message = "Login must be between 2 and 64 characters")
    private String login;

    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    private String password;

    @Size(min = 2, max = 64, message = "Firstname must be between 2 and 64 characters")
    private String firstname;

    @Size(min = 2, max = 64, message = "Lastname must be between 2 and 64 characters")
    private String lastname;
}