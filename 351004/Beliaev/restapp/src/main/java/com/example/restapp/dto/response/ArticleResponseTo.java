package com.example.restapp.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArticleResponseTo {
    private Long id;
    private Long authorId;
    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;
    private List<Long> stickerIds;
}