package com.example.Labs.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StoryResponseTo {
    private Long id;
    private Long editorId;
    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;
}
