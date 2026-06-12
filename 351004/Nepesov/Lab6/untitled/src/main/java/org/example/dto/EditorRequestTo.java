package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EditorRequestTo {
    private Long id;

    @NotBlank
    @Size(min = 2, max = 64, message = "размер должен находиться в диапазоне от 2 до 64")
    private String login;

    @NotBlank
    @Size(min = 8, max = 128, message = "размер должен находиться в диапазоне от 8 до 128")
    private String password;

    @NotBlank
    @Size(min = 2, max = 64, message = "размер должен находиться в диапазоне от 2 до 64")
    private String firstname;

    @NotBlank
    @Size(min = 2, max = 64, message = "размер должен находиться в диапазоне от 2 до 64")
    private String lastname;
}