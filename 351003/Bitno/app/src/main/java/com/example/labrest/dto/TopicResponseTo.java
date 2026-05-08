package com.example.labrest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;


@Data @NoArgsConstructor @AllArgsConstructor
public class TopicResponseTo {
    private Long id;
    private Long creatorId;
    private String title;
    private String content;
    private String created;
    private String modified;
    private Set<Long> markerIds;
}