package com.example.task361.domain.dto.request;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkerRequestTo {
    private Long id;

    @NotNull
    @Size(min = 2, max = 32)
    private String name;
}