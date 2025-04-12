package org.example.discussion.dto.responseDto;

import lombok.Data;

import java.util.UUID;

@Data
public class MessageResponseTo {
    private long id;
    private long storyId;
    private String content;
    private String country;
}
