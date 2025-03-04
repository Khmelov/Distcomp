package com.bsuir.dc.dto.request;

import jakarta.validation.constraints.Size;


public class AuthorRequestTo {
    private long id;

    @Size(min = 2, max = 64, message = "Login size must be between 2..64 characters")
    private String login;

    @Size(min = 8, max = 128, message = "Password must be between 8..128 characters")
    private String password;

    @Size(min = 2, max = 64, message = "Your firstname must be between 2..64 characters")
    private String firstname;

    @Size(min = 2, max = 64, message = "Your lastname must be between 2..64 characters")
    private String lastname;

    public long getId() {
        return id;
    }
    public void setId(long id) {
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
