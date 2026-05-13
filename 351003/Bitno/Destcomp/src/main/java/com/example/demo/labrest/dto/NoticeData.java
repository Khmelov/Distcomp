package com.example.demo.labrest.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class NoticeData implements Serializable {
    private Long id;
    private Long topicId;
    private String content;
}