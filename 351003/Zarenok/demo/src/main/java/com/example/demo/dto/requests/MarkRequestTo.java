package com.example.demo.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MarkRequestTo {
    @JsonProperty("name")
    private String name;

}
