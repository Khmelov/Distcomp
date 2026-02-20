package com.example.lab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MarkerRequestTo {

    @NotBlank
    @Size(min = 2, max = 32)
    private String name;

    public MarkerRequestTo(
            @JsonProperty("name") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
