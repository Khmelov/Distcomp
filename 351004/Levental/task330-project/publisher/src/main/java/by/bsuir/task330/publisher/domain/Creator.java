package by.bsuir.task330.publisher.domain;

import jakarta.persistence.*;
@Entity
@Table(name = "tbl_creator")
public class Creator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login", nullable = false, unique = true, length = 64)
    private String login;

    @Column(name = "password", nullable = false, length = 128)
    private String password;

    @Column(name = "firstname", nullable = false, length = 64)
    private String firstname;

    @Column(name = "lastname", nullable = false, length = 64)
    private String lastname;

    public Creator() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
