package by.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TagRequestTo {
    @NotBlank @Size(min = 2, max = 32) 
    private String name;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}