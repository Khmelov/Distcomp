package com.example.restservice.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MessageCreateDto {

    @Positive
    @Nullable
    Long id;

    @Positive
    Long newsId;
    @Size(min = 2, max = 2048)
    String content;
}
