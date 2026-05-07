package com.github.Lexya06.startrestapp.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserRequestTo {
    @NotBlank
    @Size(min = 2, max = 64)
    String login;

    @NotBlank
    @Size(min = 8, max = 128)
    String password;

    @NotBlank
    @Size(min = 2, max = 64)
    String firstname;

    @NotBlank
    @Size(min = 2, max = 64)
    String lastname;
}
