package com.socialnetwork.dto.request;

import com.socialnetwork.model.Role;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @Size(min = 2, max = 64, message = "Login must be between 2 and 64 characters")
    @NotNull(message = "Login is required")
    private String login;

    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    @NotNull(message = "Password is required")
    private String password;

    @Size(min = 2, max = 64, message = "Firstname must be between 2 and 64 characters")
    @NotNull(message = "Firstname is required")
    private String firstname;

    @Size(min = 2, max = 64, message = "Lastname must be between 2 and 64 characters")
    @NotNull(message = "Lastname is required")
    private String lastname;

    private Role role = Role.CUSTOMER;

    public RegisterRequest() {}

    public RegisterRequest(String login, String password, String firstname, String lastname, Role role) {
        this.login = login;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.role = role != null ? role : Role.CUSTOMER;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role != null ? role : Role.CUSTOMER;
    }
}

