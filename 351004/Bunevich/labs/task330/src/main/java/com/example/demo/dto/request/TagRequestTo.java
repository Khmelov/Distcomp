package com.example.demo.dto.request;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonRootName("tag")
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class TagRequestTo {
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 32, message = "Tag name must be between 2 and 32 characters")
    public String name;
}
