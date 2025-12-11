package com.task.rest.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MarkRequestTo {
    @NotBlank(message = "Name cannot be blank")
    @Size(max = 32, message = "Name must be at most 32 characters")
    private String name;

    public MarkRequestTo() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
