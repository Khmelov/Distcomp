package com.lizaveta.notebook.model.dto.request;

import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonRootName("writer")
public record WriterRequestTo(
        @NotBlank(message = "Login must not be blank")
        @Size(min = 2, max = 64, message = "Login length must be between 2 and 64")
        String login,

        @NotBlank(message = "Password must not be blank")
        @Size(min = 8, max = 128, message = "Password length must be between 8 and 128")
        String password,

        @NotBlank(message = "Firstname must not be blank")
        @Size(min = 2, max = 64, message = "Firstname length must be between 2 and 64")
        String firstname,

        @NotBlank(message = "Lastname must not be blank")
        @Size(min = 2, max = 64, message = "Lastname length must be between 2 and 64")
        String lastname) {
}
