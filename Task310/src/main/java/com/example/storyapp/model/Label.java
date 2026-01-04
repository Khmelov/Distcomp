package com.example.storyapp.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Label extends BaseEntity {
    @NotBlank @Size(min = 2, max = 64)
    private String name;

    public Label() {}
    public Label(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}