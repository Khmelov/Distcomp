package com.task.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarkRequestTo {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 32, message = "Name must be between 2 and 32 characters")
    private String name;
}