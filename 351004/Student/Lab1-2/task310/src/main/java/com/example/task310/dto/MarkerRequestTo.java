package com.example.task310.dto;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MarkerRequestTo {
    private Long id;
    @Size(min = 2, max = 32)
    private String name;
}