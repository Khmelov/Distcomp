package com.bsuir.dc.dto.request;

import jakarta.validation.constraints.Size;

public class LabelRequestTo {
    private long id;

    @Size(min = 2, max = 32, message = "Name size must be between 2..64 characters")
    private String name;

    public void setId(long id) { this.id = id; }
    public long getId() { return id; }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }
}
