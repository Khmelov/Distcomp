package com.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WriterRequestTo {
    @NotBlank(message = "Login cannot be blank")
    @Size(min = 2, max = 64, message = "Login must be between 2 and 64 characters")
    private String login;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    private String password;

    @NotBlank(message = "Firstname cannot be blank")
    @Size(min = 2, max = 64, message = "Firstname must be between 2 and 64 characters")
    private String firstname;

    @NotBlank(message = "Lastname cannot be blank")
    @Size(min = 2, max = 64, message = "Lastname must be between 2 and 64 characters")
    private String lastname;
}