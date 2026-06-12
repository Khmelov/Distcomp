package com.example.demo.labrest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.util.Set;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TopicResponseTo {
    private Long id;
    private Long creatorId;
    private String title;
    private String content;

    private String created;
    private String modified;

    private Set<Long> markerIds;
}