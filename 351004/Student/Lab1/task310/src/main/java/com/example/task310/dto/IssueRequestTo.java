package com.example.task310.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class IssueRequestTo {
    private Long id;
    private Long writerId;
    @Size(min = 2, max = 64)
    private String title;
    @Size(min = 4, max = 2048)
    private String content;
}