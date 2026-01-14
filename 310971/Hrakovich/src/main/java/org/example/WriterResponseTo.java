package org.example;

public class WriterResponseTo {

    private Long id;
    private String login;
    private String firstname;
    private String lastname;

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setFirstname(String firstName) {
        this.firstname = firstName;
    }

    public void setLastname(String lastName) {
        this.lastname = lastName;
    }
}