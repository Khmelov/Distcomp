package org.example.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsRequestTo {
    private Long id;
    private Long editorId;
    @Size(min = 2, max = 64)
    private String title;
    @Size(min = 4, max = 2048)
    private String content;
}