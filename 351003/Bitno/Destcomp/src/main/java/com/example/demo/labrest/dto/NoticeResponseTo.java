package com.example.demo.labrest.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class NoticeResponseTo {
    private Long id;
    private Long topicId;
    private String content;
}