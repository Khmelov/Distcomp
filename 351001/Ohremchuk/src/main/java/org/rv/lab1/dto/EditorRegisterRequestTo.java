package org.rv.lab1.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.rv.lab1.domain.EditorRole;

/**
 * Course HTTP tests send {@code firstname}/{@code lastname}; the spec also allows {@code firstName}/{@code lastName}.
 * {@code role} is optional and defaults to {@link EditorRole#CUSTOMER} in {@link org.rv.lab1.service.EditorService#register}.
 */
public record EditorRegisterRequestTo(
        @NotBlank @Size(min = 2, max = 64) String login,
        @NotBlank @Size(min = 8, max = 128) String password,
        @NotBlank @Size(min = 2, max = 64)
        @JsonProperty("firstName")
        @JsonAlias({"firstname", "FirstName"})
        String firstName,
        @NotBlank @Size(min = 2, max = 64)
        @JsonProperty("lastName")
        @JsonAlias({"lastname", "LastName"})
        String lastName,
        EditorRole role
) {
}
