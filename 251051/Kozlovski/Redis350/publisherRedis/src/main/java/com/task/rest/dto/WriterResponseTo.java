package com.task.rest.dto;

import java.time.LocalDateTime;

public class WriterResponseTo {
    private Long id;
    private String login;
    private String firstname;
    private String lastname;
    private LocalDateTime created;
    private LocalDateTime modified;

    public WriterResponseTo() {}

    public WriterResponseTo(Long id, String login, String firstname, String lastname, LocalDateTime created, LocalDateTime modified) {
        this.id = id;
        this.login = login;
        this.firstname = firstname;
        this.lastname = lastname;
        this.created = created;
        this.modified = modified;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }
    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    public LocalDateTime getCreated() { return created; }
    public void setCreated(LocalDateTime created) { this.created = created; }
    public LocalDateTime getModified() { return modified; }
    public void setModified(LocalDateTime modified) { this.modified = modified; }
}
