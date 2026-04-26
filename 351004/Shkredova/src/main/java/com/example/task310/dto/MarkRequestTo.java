package com.example.task310.dto;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MarkRequestTo {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 32, message = "Mark name must be between 2 and 32 characters")
    private String name;
}