package com.lizaveta.notebook.model.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonRootName("writer")
public record WriterRegistrationV2To(
        @NotBlank(message = "Login must not be blank")
        @Size(min = 2, max = 64, message = "Login length must be between 2 and 64")
        String login,

        @NotBlank(message = "Password must not be blank")
        @Size(min = 8, max = 128, message = "Password length must be between 8 and 128")
        String password,

        @NotBlank(message = "First name must not be blank")
        @JsonProperty("firstName")
        @JsonAlias({"firstname"})
        @Size(min = 2, max = 64, message = "First name length must be between 2 and 64")
        String firstName,

        @NotBlank(message = "Last name must not be blank")
        @JsonProperty("lastName")
        @JsonAlias({"lastname"})
        @Size(min = 2, max = 64, message = "Last name length must be between 2 and 64")
        String lastName,

        @Size(max = 32, message = "Role length must be at most 32")
        String role) {
}
