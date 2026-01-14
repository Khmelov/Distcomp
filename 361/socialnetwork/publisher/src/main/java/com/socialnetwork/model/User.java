package com.socialnetwork.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_user", schema = "distcomp")
public class User extends BaseEntity {

    @Column(name = "login", nullable = false, unique = true, length = 64)
    @Size(min = 2, max = 64)
    private String login;

    @Column(name = "password", nullable = false, length = 128)
    @Size(min = 8, max = 128)
    private String password;

    @Column(name = "firstname", nullable = false, length = 64)
    @Size(min = 2, max = 64)
    private String firstname;

    @Column(name = "lastname", nullable = false, length = 64)
    @Size(min = 2, max = 64)
    private String lastname;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role = Role.CUSTOMER;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Tweet> tweets = new ArrayList<>();

    public User() {
        super();
    }

    public User(String login, String password, String firstname, String lastname) {
        this.login = login;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.role = Role.CUSTOMER;
    }

    public User(String login, String password, String firstname, String lastname, Role role) {
        this.login = login;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.role = role;
    }

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

    public List<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(List<Tweet> tweets) {
        this.tweets = tweets;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}