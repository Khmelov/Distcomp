package com.blog.dto.response;

public class TagResponseTo {
    private Long id;
    private String name;

    // Конструкторы
    public TagResponseTo() {}

    public TagResponseTo(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}