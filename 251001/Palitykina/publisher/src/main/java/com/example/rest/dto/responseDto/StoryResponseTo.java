package com.example.rest.dto.responseDto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class StoryResponseTo {
    private long id;
    private long userId;
    private String title;
    private String content;
    private OffsetDateTime created;
    private OffsetDateTime modified;
}
