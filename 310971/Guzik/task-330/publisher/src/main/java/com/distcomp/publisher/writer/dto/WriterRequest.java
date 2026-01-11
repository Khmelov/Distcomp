package com.distcomp.publisher.writer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class WriterRequest {

    @NotBlank
    @Size(max = 64)
    private String login;

    @NotBlank
    @Size(max = 128)
    private String password;

    @NotBlank
    @Size(max = 64)
    private String firstname;

    @NotBlank
    @Size(max = 64)
    private String lastname;

    public WriterRequest() {
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
