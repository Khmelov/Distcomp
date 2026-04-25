package com.example.demo.dto.request;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonRootName("writer")
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
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
