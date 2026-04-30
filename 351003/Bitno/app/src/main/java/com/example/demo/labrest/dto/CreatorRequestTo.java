package com.example.demo.labrest.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class CreatorRequestTo {
    @NotBlank @Size(min = 2, max = 64) private String login;
    @NotBlank @Size(min = 8, max = 128) private String password;
    @NotBlank @Size(min = 2, max = 64) private String firstname;
    @NotBlank @Size(min = 2, max = 64) private String lastname;
}