package com.example.demo.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_writer", schema = "distcomp")
public class Writer extends BaseEntity{
    @Column(nullable = false, unique = true, length = 64)
    private String login;
    @Column(nullable = false, length = 128)
    private String password;
    @Column(nullable = false, length = 64)
    private String firstname;
    @Column(nullable = false, length = 64)
    private String lastname;

    // Геттеры
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

    // Сеттеры
    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
