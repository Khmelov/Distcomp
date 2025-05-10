package com.example.restservice.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MarkerCreateDto {

    @Positive
    @Nullable
    Long id;

    @Size(min=2, max=32)
    String name;
}
