package com.example.restservice.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class NewsCreateDto {

    @Positive
    @Nullable
    Long id;

    @Positive
    Long authorId;
    @Size(min=2, max=64)
    String title;
    @Size(min=4, max=2048)
    String content;
    @Nullable
    List<String> markers;
}