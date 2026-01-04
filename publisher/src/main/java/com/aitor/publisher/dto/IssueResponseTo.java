package com.aitor.publisher.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class IssueResponseTo {
    private long id;
    private long userId;
    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;
    private List<String> stickers;
}