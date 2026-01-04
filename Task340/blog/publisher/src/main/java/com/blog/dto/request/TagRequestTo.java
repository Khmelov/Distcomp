package com.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TagRequestTo {

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 32, message = "Name must be between 2 and 32 characters")
    private String name;

    // Конструкторы
    public TagRequestTo() {}

    public TagRequestTo(String name) {
        this.name = name;
    }

    // Геттеры и сеттеры
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}