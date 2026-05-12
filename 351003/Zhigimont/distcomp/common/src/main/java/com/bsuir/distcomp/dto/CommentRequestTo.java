package com.bsuir.distcomp.dto;

import com.bsuir.types.OperationType;
import lombok.Data;

@Data
public class CommentRequestTo {
    private Long id;
    private Long topicId;
    private String content;
    private OperationType operation;

    private String correlationId;
}