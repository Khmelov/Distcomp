package com.bsuir.dc.dto.response;

public class AuthorResponseTo {
    private long id;
    private String login;
    private String password;
    private String firstname;
    private String lastname;

    public void setId(long id) { this.id = id; }
    public long getId() { return id; }

    public void setLogin(String login) { this.login = login; }
    public String getLogin() { return login; }

    public void setPassword(String password) { this.password = password; }
    public String getPassword() { return password; }

    public void setFirstname(String firstname) { this.firstname = firstname; }
    public String getFirstname() { return firstname; }

    public void setLastname(String lastname) { this.lastname = lastname; }
    public String getLastname() { return lastname; }
}
