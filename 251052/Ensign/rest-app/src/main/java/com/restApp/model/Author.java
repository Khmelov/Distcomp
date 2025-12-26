package com.restApp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Author extends BaseEntity {
    private String login;
    private String password;
    private String firstname;
    private String lastname;

    private List<News> news = new ArrayList<>();

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

    public List<News> getNews() {
        return news;
    }

    public void setNews(List<News> news) {
        this.news = news;
    }

    // Equals and HashCode call super
    @Override
    public boolean equals(Object o) {
        if (!super.equals(o))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
