package com.adashkevich.rest.lab.model;

public class Editor extends BaseEntity {
    private String login;
    private String password;
    private String firstname;
    private String lastname;
    private Role role = Role.CUSTOMER;

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
