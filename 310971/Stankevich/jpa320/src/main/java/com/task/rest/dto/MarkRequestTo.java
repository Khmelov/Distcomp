package com.task.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarkRequestTo {

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 1, max = 32, message = "Name must be between 1 and 32 characters")
    private String name;
}