package com.blog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_editor", schema = "distcomp")
public class Editor extends BaseEntity {

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

    @Column(name = "role", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EditorRole role = EditorRole.CUSTOMER;

    @OneToMany(mappedBy = "editor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Topic> topics = new ArrayList<>();

    // Конструкторы
    public Editor() {
        super();
    }

    public Editor(String login, String password, String firstname, String lastname, EditorRole role) {
        this.login = login;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.role = role;
    }

    // Геттеры и сеттеры
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

    public EditorRole getRole() {
        return role;
    }

    public void setRole(EditorRole role) {
        this.role = role;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }
}