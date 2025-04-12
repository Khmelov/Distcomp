package com.example.rest.dto;

import lombok.Data;

public class TagRequestTo {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
