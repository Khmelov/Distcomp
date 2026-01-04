package com.blog.dto.response;

import com.blog.model.EditorRole;

public class EditorResponseTo {
    private Long id;
    private String login;
    private String firstname;
    private String lastname;
    private EditorRole role;

    public EditorResponseTo() {}

    public EditorResponseTo(Long id, String login, String firstname, String lastname, EditorRole role) {
        this.id = id;
        this.login = login;
        this.firstname = firstname;
        this.lastname = lastname;
        this.role = role;
    }

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

    public EditorRole getRole() {
        return role;
    }

    public void setRole(EditorRole role) {
        this.role = role;
    }
}