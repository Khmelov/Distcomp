package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EditorRequestTo {
    private Long id;

    @NotBlank
    @Size(min = 2, max = 64)
    private String login;

    @NotBlank
    @Size(min = 4, max = 128)
    private String password;

    private String firstname;
    private String lastname;
}