package by.distcomp.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NoteRequestTo (
        @NotNull
        Long articleId,
        @NotBlank
        @Size(min = 4, max = 2048)
        String content
){ }
