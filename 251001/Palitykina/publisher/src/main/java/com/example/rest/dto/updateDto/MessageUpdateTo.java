package com.example.rest.dto.updateDto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MessageUpdateTo {
    private long id;
    private long storyId;

    @Size(min = 2, max = 2048)
    private String content;
}
