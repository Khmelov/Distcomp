package com.example.task310.dto;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostRequestTo {
    private Long id;
    private Long issueId;
    @Size(min = 2, max = 2048)
    private String content;
}