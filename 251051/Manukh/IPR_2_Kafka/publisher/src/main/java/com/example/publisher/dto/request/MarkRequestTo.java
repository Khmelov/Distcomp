// MarkRequestTo.java
package com.example.publisher.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MarkRequestTo {
    @NotBlank(message = "Name cannot be empty")
    @Size(min = 2, max = 32, message = "Name must be between 2 and 32 characters")
    private String name;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}