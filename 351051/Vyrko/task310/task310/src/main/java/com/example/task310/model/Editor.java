
package com.example.task310.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Модель редактора, содержит его личные данные для работы с темами.
public class Editor {

    private Long id;

    @NotBlank
    private String login;

    @NotBlank
    @Size(min = 8, max = 128)
    private String password;

    @NotBlank
    private String firstname;

    @NotBlank
    private String lastname;

    // Конструктор без параметров используется при создании объекта.
    public Editor() {
    }

    // Конструктор для создания редактора со всеми полями.
    public Editor(Long id, String login, String password,
                  String firstname, String lastname) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    // Методы доступа к полям модели.
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
