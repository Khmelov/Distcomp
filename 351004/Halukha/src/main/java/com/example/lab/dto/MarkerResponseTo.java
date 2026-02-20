package com.example.lab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MarkerResponseTo {

    private Long id;
    private String name;

    public MarkerResponseTo(
            @JsonProperty("id") Long id,
            @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
