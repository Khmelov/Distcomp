package com.example.task320.dto;

// Данные маркера, которые возвращаются клиенту.
public class MarkerResponseTo {

    private Long id;
    private String name;

    // Конструктор без параметров.
    public MarkerResponseTo() {
    }

    // Конструктор для заполнения всех полей ответа.
    public MarkerResponseTo(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Методы доступа к полям DTO.
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