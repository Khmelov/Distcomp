package com.example.publisher.model;

import java.time.LocalDateTime;

public class Editor extends BaseEntity {
    private String login;
    private String password;
    private String firstname;
    private String lastname;
    private LocalDateTime created;
    private LocalDateTime modified;

    public String getLogin() { return login; }
    public void setLogin(String login) {
        this.login = login;
        this.modified = LocalDateTime.now();
    }

    public String getPassword() { return password; }
    public void setPassword(String password) {
        this.password = password;
        this.modified = LocalDateTime.now();
    }

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) {
        this.firstname = firstname;
        this.modified = LocalDateTime.now();
    }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) {
        this.lastname = lastname;
        this.modified = LocalDateTime.now();
    }

    public LocalDateTime getCreated() { return created; }
    public void setCreated(LocalDateTime created) { this.created = created; }

    public LocalDateTime getModified() { return modified; }
    public void setModified(LocalDateTime modified) { this.modified = modified; }
}