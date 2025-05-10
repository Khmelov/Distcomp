package com.example.restservice.dto;

import com.example.restservice.model.Status;
import lombok.Data;

@Data
public class MessageResponseDto {
    private Long id;
    private String content;
    private Long newsId;
    private Status status;
}
