package org.example.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsResponseTo {
    private Long id;
    private Long editorId;
    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;
}