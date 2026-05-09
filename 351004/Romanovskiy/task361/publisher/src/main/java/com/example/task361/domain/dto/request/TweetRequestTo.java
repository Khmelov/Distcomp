package com.example.task361.domain.dto.request;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List; // ВАЖНО: добавить импорт

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TweetRequestTo {
    private Long id;

    @NotNull
    private Long authorId;

    @NotNull
    @Size(min = 2, max = 64)
    private String title;

    @NotNull
    @Size(min = 4, max = 2048)
    private String content;

    // ДОБАВЬ ЭТУ СТРОКУ:
    private List<String> markers; 
}