package com.example.labrest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class Notice {
    private Long id;
    private Long topicId;
    private String content;
}