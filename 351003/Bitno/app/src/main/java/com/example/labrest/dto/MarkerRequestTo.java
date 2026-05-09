package com.example.labrest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
@Data @NoArgsConstructor @AllArgsConstructor
public class MarkerRequestTo {
    @NotBlank @Size(min = 2, max = 32) private String name;
}
