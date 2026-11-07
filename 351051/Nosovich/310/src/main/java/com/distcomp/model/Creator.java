package com.distcomp.model;

/**
 * Author of the issues (the "one" side of the Creator-Issue relation).
 */
public class Creator extends AbstractEntity {

    private String login;

    private String password;

    private String firstname;

    private String lastname;

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

    @Override
    public String toString() {
        return "Creator{id=" + getId() + ", login='" + login + "'}";
    }
}
