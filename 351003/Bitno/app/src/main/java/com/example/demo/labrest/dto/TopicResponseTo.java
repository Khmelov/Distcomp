package com.example.demo.labrest.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data @NoArgsConstructor @AllArgsConstructor
public class TopicResponseTo {
    private Long id;
    private Long creatorId;
    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;
    private Set<Long> markerIds;
}