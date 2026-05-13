package com.bsuir.distcomp.dto;

import lombok.Data;

import java.util.List;

@Data
public class CommentListResponseTo {
    private List<CommentResponseTo> comments;
    private String correlationId;
}
