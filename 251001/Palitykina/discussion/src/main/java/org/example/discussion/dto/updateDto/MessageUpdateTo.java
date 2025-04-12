package org.example.discussion.dto.updateDto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class MessageUpdateTo {
    private long id;
    private long storyId;
    private String country;
    @Size(min = 2, max = 2048)
    private String content;
}
