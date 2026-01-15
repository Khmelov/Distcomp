package com.example.task310.dto.response;

import java.time.OffsetDateTime;

public record NewsResponseTo(
        Long id,
        Long writerId,
        String title,
        String content,
        OffsetDateTime created,
        OffsetDateTime modified
) {}
