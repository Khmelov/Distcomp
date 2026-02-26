package com.example.restapp.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NoteRequestTo {
    @NotNull
    private Long articleId;

    @Size(min = 2, max = 2048)
    private String content;
}