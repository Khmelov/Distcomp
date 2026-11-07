package com.example.task320.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Данные маркера, которые приходят от клиента.
public class MarkerRequestTo {

    @NotBlank
    @Size(min = 2, max = 32)
    private String name;

    // Конструктор без параметров.
    public MarkerRequestTo() {
    }

    // Конструктор для создания запроса с именем маркера.
    public MarkerRequestTo(String name) {
        this.name = name;
    }

    // Методы доступа к полю DTO.
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}