package com.example.demo.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.time.ZonedDateTime;

@Value
public class MarkResponseTo {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("created")
    private ZonedDateTime created;
    @JsonProperty("modified")
    private ZonedDateTime modified;
}
