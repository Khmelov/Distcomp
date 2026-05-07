package by.bsuir.distcomp.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthorRequestTo {
    private Long id;

    @NotBlank(message = "Login is required")
    @Size(min = 2, max = 64, message = "Login must be between 2 and 64 characters")
    @JsonProperty("login")
    private String login;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    @JsonProperty("password")
    private String password;

    @Size(max = 64, message = "Firstname must not exceed 64 characters")
    @JsonProperty("firstname")
    private String firstname;

    @Size(max = 64, message = "Lastname must not exceed 64 characters")
    @JsonProperty("lastname")
    private String lastname;

    public AuthorRequestTo() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
