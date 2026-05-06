package by.boukhvalova.distcomp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestTo {
    @NotBlank
    private String login;
    @NotBlank
    private String password;
}

