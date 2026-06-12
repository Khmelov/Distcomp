package com.example.demo.labrest.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notice {

    private Long id;
    private Long topicId;
    private String content;
}