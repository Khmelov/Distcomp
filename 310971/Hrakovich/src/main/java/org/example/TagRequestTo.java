package org.example;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TagRequestTo {

    @NotBlank
    @Size(min = 2, max = 32)
    @JsonDeserialize(using = StrictStringDeserializer.class)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}