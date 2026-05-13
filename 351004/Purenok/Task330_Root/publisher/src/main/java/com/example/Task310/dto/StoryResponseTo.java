package com.example.Task310.dto;

import java.time.LocalDateTime;
import java.util.List;

public record StoryResponseTo(
        Long id,
        Long editorId,
        String title,
        String content,
        LocalDateTime created,
        LocalDateTime modified,
        List<MarkerResponseTo> markers
<<<<<<< HEAD:351004/Purenok/Task330_Root/publisher/src/main/java/com/example/Task310/dto/StoryResponseTo.java


        
=======
>>>>>>> e9b46436b12a679a1122bfd9ca7840c196ff410a:351004/Purenok/Task310/src/main/java/com/example/Task310/dto/StoryResponseTo.java
) {}