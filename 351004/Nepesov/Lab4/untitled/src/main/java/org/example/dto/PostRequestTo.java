package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestTo {

    // Для обновления (PUT) это поле придет из тестов
    private Long id;

    @NotNull(message = "News ID cannot be null")
    private Long newsId;

    @NotBlank(message = "Content cannot be blank")
    @Size(min = 2, max = 2048, message = "Content size must be between 2 and 2048")
    private String content;
}