package by.bsuir.distcomp.dto;

import by.bsuir.distcomp.model.UserRole;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WriterDto(
        Long id,
        @NotBlank @Size(min = 2, max = 64) String login,
        @NotBlank @Size(min = 8, max = 128) String password,
        @JsonAlias("firstName")
        @NotBlank @Size(min = 2, max = 64) String firstname,
        @JsonAlias("lastName")
        @NotBlank @Size(min = 2, max = 64) String lastname,
        UserRole role
) {
    public WriterDto(Long id, String login, String password, String firstname, String lastname) {
        this(id, login, password, firstname, lastname, null);
    }
}
