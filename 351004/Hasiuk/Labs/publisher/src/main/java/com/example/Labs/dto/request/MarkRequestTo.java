package com.example.Labs.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MarkRequestTo {
    @NotBlank
    @Size(min = 2, max = 32)
    private String name;
}