package com.example.demo.dto.request;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.validation.constraints.Size;

@Data
@JsonRootName("post")
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class PostRequestTo {
    @NotNull(message = "can't be null")
    public long storyId;    // TODO: контроль существования storyID
    @NotBlank(message = "Content cannot be blank")
    @Size(min = 4, max = 2048, message = "Content must be between 4 and 2048 characters")
    public String content;
}
