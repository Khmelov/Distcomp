package by.bsuir.task310.dto;

import lombok.Data;

@Data
public class TopicRequestTo {
    private Long id;
    private Long authorId;
    private String title;
    private String content;
}