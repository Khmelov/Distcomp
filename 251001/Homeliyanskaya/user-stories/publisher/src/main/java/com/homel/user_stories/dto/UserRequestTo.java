package com.homel.user_stories.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class UserRequestTo {
    private Long id;

    @NotBlank
    @Size(min = 2, max = 64)
    private String login;

    @NotBlank
    @Size(min = 8, max = 128)
    private String password;

    @NotBlank
    @Size(min = 2, max = 64)
    private String firstname;

    @NotBlank
    @Size(min = 2, max = 64)
    private String lastname;
}
