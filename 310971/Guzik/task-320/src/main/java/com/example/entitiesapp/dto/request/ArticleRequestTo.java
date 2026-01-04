package com.example.entitiesapp.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleRequestTo {
    @NotNull(message = "writerId is required")
    private Long writerId;

    @Size(min = 2, max = 64, message = "Title must be between 2 and 64 characters")
    private String title;

    @Size(min = 2, max = 2048, message = "Content must be between 2 and 2048 characters")
    private String content;

    // Конструктор для ручной проверки типа
    @JsonCreator
    public ArticleRequestTo(
            @JsonProperty(value = "writerId", required = true) Long writerId,
            @JsonProperty(value = "title", required = true) String title,
            @JsonProperty(value = "content", required = true) Object content) {

        this.writerId = writerId;
        this.title = title;

        // Критически важная проверка: content не должен быть числом
        if (content instanceof Number) {
            throw new IllegalArgumentException("Content must be a string, not a number. Received: " + content);
        }

        if (content == null) {
            throw new IllegalArgumentException("Content is required");
        }

        this.content = content.toString();

        // Проверяем длину
        if (this.content.length() < 2 || this.content.length() > 2048) {
            throw new IllegalArgumentException("Content must be between 2 and 2048 characters. Length: " + this.content.length());
        }
    }

    // Конструктор без аргументов для Spring
    public ArticleRequestTo() {}
}