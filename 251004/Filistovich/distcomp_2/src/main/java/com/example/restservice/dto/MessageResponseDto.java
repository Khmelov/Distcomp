package com.example.restservice.dto;

import com.example.restservice.model.Status;
import lombok.Data;

import java.io.Serializable;

@Data
public class MessageResponseDto implements Serializable {
    private Long id;
    private String content;
    private Long newsId;
    private Status status;
}
