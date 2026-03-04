package com.example.demo.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MarkRequestTo {

    @NotBlank
    @Size(min = 2, max = 50)
    @JsonProperty("name")
    private String name;

}
