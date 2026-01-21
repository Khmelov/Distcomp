package com.example.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tbl_tag")
public class Tag extends BaseEntity {
    @NotBlank @Size(min = 2, max = 32)
    @Column(name = "name", nullable = false, unique = true, length = 32)
    private String name;

    //конструктор
    public Tag() {}
    public Tag(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    //геттеры и сеттеры
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}