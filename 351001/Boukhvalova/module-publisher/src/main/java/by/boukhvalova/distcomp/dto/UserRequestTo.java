package by.boukhvalova.distcomp.dto;

import by.boukhvalova.distcomp.entities.UserRole;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequestTo {
    long id;

    @Size(min = 2, max = 64)
    String login;

    @Size(min = 8, max = 128)
    String password;

    @Size(min = 2, max = 64)
    @JsonProperty("firstname")
    @JsonAlias({"firstName"})
    String firstname;

    @Size(min = 2, max = 64)
    @JsonProperty("lastname")
    @JsonAlias({"lastName"})
    String lastname;

    UserRole role;
}
