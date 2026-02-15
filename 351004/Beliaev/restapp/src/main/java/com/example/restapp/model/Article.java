package com.example.restapp.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class Article extends BaseEntity {
    private Long authorId;
    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;
    private List<Long> stickerIds = new ArrayList<>();
}