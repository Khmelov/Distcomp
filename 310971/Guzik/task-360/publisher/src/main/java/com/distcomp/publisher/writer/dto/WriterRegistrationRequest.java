package com.distcomp.publisher.writer.dto;

import com.distcomp.publisher.writer.domain.WriterRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class WriterRegistrationRequest {

    @NotBlank
    @Size(min = 2, max = 64)
    private String login;

    @NotBlank
    @Size(min = 8, max = 128)
    private String password;

    @NotBlank
    @Size(min = 2, max = 64)
    private String firstname;

    @NotBlank
    @Size(min = 2, max = 64)
    private String lastname;

    private WriterRole role;

    public WriterRegistrationRequest() {
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

    public WriterRole getRole() {
        return role;
    }

    public void setRole(WriterRole role) {
        this.role = role;
    }
}
