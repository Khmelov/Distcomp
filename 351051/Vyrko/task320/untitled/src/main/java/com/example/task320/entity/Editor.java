package com.example.task320.entity;

import jakarta.persistence.*;
import java.util.List;

// Сущность редактора, соответствующая таблице tbl_editor в базе данных
@Entity
@Table(name = "tbl_editor")
public class Editor {

    // Уникальный идентификатор редактора, генерируемый базой данных
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Уникальный логин редактора
    @Column(nullable = false, unique = true)
    private String login;

    // Пароль редактора
    private String password;

    // Имя редактора
    private String firstname;

    // Фамилия редактора
    private String lastname;

    // При удалении редактора удаляются все его темы.
    @OneToMany(mappedBy = "editor", cascade = CascadeType.REMOVE)
    private List<Topic> topics;

    // Конструктор без параметров, необходимый для JPA
    public Editor() {
    }

    // Конструктор для создания редактора с основными данными
    public Editor(String login, String password, String firstname, String lastname) {
        this.login = login;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    // Возвращает идентификатор редактора
    public Long getId() {
        return id;
    }

    // Устанавливает идентификатор редактора
    public void setId(Long id) {
        this.id = id;
    }

    // Возвращает логин редактора
    public String getLogin() {
        return login;
    }

    // Устанавливает логин редактора
    public void setLogin(String login) {
        this.login = login;
    }

    // Возвращает пароль редактора
    public String getPassword() {
        return password;
    }

    // Устанавливает пароль редактора
    public void setPassword(String password) {
        this.password = password;
    }

    // Возвращает имя редактора
    public String getFirstname() {
        return firstname;
    }

    // Устанавливает имя редактора
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    // Возвращает фамилию редактора
    public String getLastname() {
        return lastname;
    }

    // Устанавливает фамилию редактора
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    // Возвращает список тем редактора
    public List<Topic> getTopics() {
        return topics;
    }

    // Устанавливает список тем редактора
    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }
}
