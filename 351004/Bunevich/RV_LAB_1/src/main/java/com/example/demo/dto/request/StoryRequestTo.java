package com.example.demo.dto.request;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonRootName("writer")
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class StoryRequestTo {
    //TODO: writerId проверка при создании запроса
    @NotNull(message = "Writer ID cannot be null")
    public long writerId;
    @NotBlank(message = "Title cannot be blank")
    @Size(min = 2, max = 64, message = "Title must be between 2 and 64 characters")
    public String title;
    @NotBlank(message = "Content cannot be blank")
    @Size(min = 4, max = 2048, message = "Content must be between 4 and 2048 characters")
    public String content;
    //TODO: даты(created, modif) нужно сервером задать
    private List<Long> tagIds;
}
