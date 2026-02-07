package by.distcomp.task1.dto;

import java.util.List;

public record ArticleRequestTo(
        Long userId,
        String title,
        String content,
        List<Long> stickerIds
) { }
