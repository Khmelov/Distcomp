package com.distcomp.publisher.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tbl_creator")
public class Creator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 64)
    @Column(unique = true)
    private String login;

    @NotBlank
    @Size(min = 8, max = 128)
    private String password;

    @NotBlank
    @Size(min = 2, max = 64)
    private String firstname;

    @NotBlank
    @Size(min = 2, max = 64)
    private String lastname;

    public Creator() {}

    public Creator(Long id, String login, String password, String firstname, String lastname) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
}