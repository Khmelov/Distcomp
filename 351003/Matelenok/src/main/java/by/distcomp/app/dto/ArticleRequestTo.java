package by.distcomp.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ArticleRequestTo(
        @NotNull
        Long userId,
        @NotBlank
        @Size(min = 2, max = 64)
        String title,
        @NotBlank
        @Size(min = 4, max = 2048)
        String content,
        List<Long> stickerIds
) { }
