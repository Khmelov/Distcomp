package com.lizaveta.notebook.model.entity;

public final class Writer {

    private final Long id;
    private final String login;
    private final String password;
    private final String firstname;
    private final String lastname;

    public Writer(
            final Long id,
            final String login,
            final String password,
            final String firstname,
            final String lastname) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
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

    public Writer withId(final Long newId) {
        return new Writer(newId, login, password, firstname, lastname);
    }

    public Writer withLogin(final String newLogin) {
        return new Writer(id, newLogin, password, firstname, lastname);
    }

    public Writer withPassword(final String newPassword) {
        return new Writer(id, login, newPassword, firstname, lastname);
    }

    public Writer withFirstname(final String newFirstname) {
        return new Writer(id, login, password, newFirstname, lastname);
    }

    public Writer withLastname(final String newLastname) {
        return new Writer(id, login, password, firstname, newLastname);
    }
}
