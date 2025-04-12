package com.example.rest.dto.requestDto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MessageRequestTo {

    private long storyId;

    @Size(min = 2, max = 2048)
    private String content;
}
