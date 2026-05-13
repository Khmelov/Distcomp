package com.example.demo.labrest.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class MarkerRequestTo {
    @NotBlank @Size(min = 2, max = 32) private String name;
}