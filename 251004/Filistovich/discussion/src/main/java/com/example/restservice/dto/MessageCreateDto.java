package com.example.restservice.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageCreateDto {

    @Positive
    @Nullable
    Long id;

    @Positive
    Long newsId;
    @Size(min = 2, max = 2048)
    String content;
}
