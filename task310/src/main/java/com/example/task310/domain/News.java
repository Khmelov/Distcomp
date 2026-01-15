package com.example.task310.domain;

import java.time.OffsetDateTime;

public record News(
        Long id,
        Long writerId,
        String title,
        String content,
        OffsetDateTime created,
        OffsetDateTime modified
) {}
