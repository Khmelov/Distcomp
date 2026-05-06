package com.example.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonRootName("tag")
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class TagResponseTo {
    private long id;
    private String name;
}
