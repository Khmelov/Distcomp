package com.distcomp.publisher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MarkerRequestDTO {
    private Long id;

    @NotBlank
    @Size(min = 2, max = 32)
    private String name;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}