package by.bsuir.distcomp.dto.request;

import by.bsuir.distcomp.model.EditorRole;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EditorRegistrationTo {

    @NotBlank
    @Size(min = 2, max = 64)
    private String login;

    @NotBlank
    @Size(min = 8, max = 128)
    private String password;

    @NotBlank
    @Size(min = 2, max = 64)
    @JsonProperty("firstName")
    @JsonAlias("firstname")
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 64)
    @JsonProperty("lastName")
    @JsonAlias("lastname")
    private String lastName;

    private EditorRole role;

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public EditorRole getRole() {
        return role;
    }

    public void setRole(EditorRole role) {
        this.role = role;
    }
}
