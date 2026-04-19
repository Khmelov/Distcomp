package com.example.demo.labrest.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class NoticeResponseTo {
    private Long id;
    private Long topicId;
    private String content;
}