package com.task.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class WriterRequestTo {
    @NotBlank(message = "Login cannot be blank")
    @Size(min = 4, max = 64, message = "Login must be at most 64 characters")
    private String login;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    private String password;

    @NotBlank(message = "Firstname cannot be blank")
    @Size(max = 64, message = "Firstname must be at most 64 characters")
    private String firstname;

    @NotBlank(message = "Lastname cannot be blank")
    @Size(max = 64, message = "Lastname must be at most 64 characters")
    private String lastname;

    public WriterRequestTo() {}

    // Getters and Setters
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }
    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
}
