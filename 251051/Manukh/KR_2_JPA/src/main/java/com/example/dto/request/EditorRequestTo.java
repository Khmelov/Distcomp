package com.example.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EditorRequestTo {

    @NotBlank(message = "Login cannot be empty")
    @Size(min = 2, max = 64, message = "Login must be between 2 and 64 characters")
    private String login;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    private String password;

    @NotBlank(message = "First name cannot be empty")
    @Size(min = 2, max = 64, message = "First name must be between 2 and 64 characters")
    private String firstname;

    @NotBlank(message = "Last name cannot be empty")
    @Size(min = 2, max = 64, message = "Last name must be between 2 and 64 characters")
    private String lastname;

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
}