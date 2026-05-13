package com.bsuir.distcomp.dto;

import com.bsuir.types.Status;
import lombok.Data;

@Data
public class CommentResponseTo {
    private Long id;
    private Long topicId;
    private String content;
    private Status status;

    private String correlationId;
}