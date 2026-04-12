package com.example.Task310.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class StoryRequestTo {
    private Long id;

    @NotNull
    private Long editorId;

    @NotBlank
    @Size(min = 2, max = 64)
    private String title;

    @NotBlank
    @Size(min = 4, max = 2048)
    private String content;

    // ТЕСТ отправляет ["red70", "green70"], поэтому здесь должен быть String
    private List<String> markers;
}