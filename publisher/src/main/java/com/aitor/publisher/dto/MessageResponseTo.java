package com.aitor.publisher.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseTo implements Serializable {
    private Long id;
    private Long issueId;
    private String content;
}