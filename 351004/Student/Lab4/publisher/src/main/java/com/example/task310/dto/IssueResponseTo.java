package com.example.task310.dto;
import java.time.LocalDateTime;

public record IssueResponseTo(
        Long id,
        Long writerId,
        String title,
        String content,
        LocalDateTime created,
        LocalDateTime modified
) {}