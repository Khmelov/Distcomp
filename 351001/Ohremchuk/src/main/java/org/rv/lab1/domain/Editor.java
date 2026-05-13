package org.rv.lab1.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_editor")
public class Editor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String login;

    @Column(nullable = false, length = 128)
    private String password;

    @Column(nullable = false, length = 64)
    private String firstname;

    @Column(nullable = false, length = 64)
    private String lastname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private EditorRole role = EditorRole.CUSTOMER;

    @OneToMany(mappedBy = "editor", orphanRemoval = true)
    private List<Story> stories = new ArrayList<>();

    protected Editor() {
    }

    public Editor(String login, String password, String firstname, String lastname, EditorRole role) {
        this.login = login;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.role = role != null ? role : EditorRole.CUSTOMER;
    }

    public Long getId() {
        return id;
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

    public EditorRole getRole() {
        return role;
    }

    public void setRole(EditorRole role) {
        this.role = role;
    }
}

