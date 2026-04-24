package com.lizaveta.notebook.model.entity;

import com.lizaveta.notebook.model.UserRole;

public final class Writer {

    private final Long id;
    private final String login;
    private final String password;
    private final String firstname;
    private final String lastname;
    private final UserRole role;

    public Writer(
            final Long id,
            final String login,
            final String password,
            final String firstname,
            final String lastname,
            final UserRole role) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public UserRole getRole() {
        return role;
    }

    public Writer withId(final Long newId) {
        return new Writer(newId, login, password, firstname, lastname, role);
    }

    public Writer withLogin(final String newLogin) {
        return new Writer(id, newLogin, password, firstname, lastname, role);
    }

    public Writer withPassword(final String newPassword) {
        return new Writer(id, login, newPassword, firstname, lastname, role);
    }

    public Writer withFirstname(final String newFirstname) {
        return new Writer(id, login, password, newFirstname, lastname, role);
    }

    public Writer withLastname(final String newLastname) {
        return new Writer(id, login, password, firstname, newLastname, role);
    }

    public Writer withRole(final UserRole newRole) {
        return new Writer(id, login, password, firstname, lastname, newRole);
    }
}
