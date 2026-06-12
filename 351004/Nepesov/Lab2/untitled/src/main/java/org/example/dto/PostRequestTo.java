package org.example.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestTo {
    private Long id;
    private Long newsId;

    @Size(min = 2, max = 2048)
    private String content;
}