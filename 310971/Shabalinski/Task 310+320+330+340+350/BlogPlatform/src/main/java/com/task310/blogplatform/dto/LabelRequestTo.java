package com.task310.blogplatform.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LabelRequestTo {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

