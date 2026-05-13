package com.example.kafkademo.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class MarkerRequestDto {
    @NotBlank
    @Size(min = 1, max = 50)
    private String name;
}