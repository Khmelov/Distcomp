package com.dc.anotherone.model.dto;

public class AuthorRequestTo {

    private Long id;

    private String login;

    private String password;

    private String firstname;

    private String lastname;

    public AuthorRequestTo(Long id, String login, String password, String firstname, String lastname) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public AuthorRequestTo(String login, String password, String firstname, String lastname) {
        this.login = login;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    private AuthorRequestTo() {
    }

    public String getLogin() {
        return this.login;
    }

    public String getPassword() {
        return this.password;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
