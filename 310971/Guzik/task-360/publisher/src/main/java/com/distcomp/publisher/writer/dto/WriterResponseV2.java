package com.distcomp.publisher.writer.dto;

import com.distcomp.publisher.writer.domain.WriterRole;

public class WriterResponseV2 {

    private long id;
    private String login;
    private String firstname;
    private String lastname;
    private WriterRole role;

    public WriterResponseV2() {
    }

    public WriterResponseV2(long id, String login, String firstname, String lastname, WriterRole role) {
        this.id = id;
        this.login = login;
        this.firstname = firstname;
        this.lastname = lastname;
        this.role = role;
    }

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
