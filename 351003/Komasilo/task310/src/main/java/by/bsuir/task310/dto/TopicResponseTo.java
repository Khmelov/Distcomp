package by.bsuir.task310.dto;

import lombok.Data;

@Data
public class TopicResponseTo {
    private Long id;
    private Long authorId;
    private String title;
    private String content;
    private String created;
    private String modified;
}