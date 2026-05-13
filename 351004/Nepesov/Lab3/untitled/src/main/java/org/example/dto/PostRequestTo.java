package org.example.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class PostRequestTo {
    private Long id;
    private Long newsId;
    private String content;
}