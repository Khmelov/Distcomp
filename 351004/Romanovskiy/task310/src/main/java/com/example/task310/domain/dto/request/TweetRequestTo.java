package com.example.task310.domain.dto.request;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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
}