package com.example.task310.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Модель маркера, который используется для обозначения тем.
public class Marker {

    private Long id;

    @NotBlank
    @Size(min = 2, max = 32)
    private String name;

    // Конструктор без параметров.
    public Marker() {
    }

    // Конструктор для создания маркера с заданными значениями.
    public Marker(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Методы доступа к полям модели.
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