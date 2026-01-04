package com.example.storyapp.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class User extends BaseEntity {
    @NotBlank @Size(min = 2, max = 32)
    private String login;
    @NotBlank @Size(min = 6, max = 128)
    private String password;
    @Size(min = 1, max = 64)
    private String firstname;
    @Size(min = 1, max = 64)
    private String lastname;

    //конструктор
    public User() {}
    public User(Long id, String login, String password, String firstname, String lastname) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    //геттеры и сеттеры
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
}