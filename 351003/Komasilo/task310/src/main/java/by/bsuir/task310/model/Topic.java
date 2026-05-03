package by.bsuir.task310.model;

import lombok.Data;

@Data
public class Topic {
    private Long id;
    private Long authorId;
    private String title;
    private String content;
    private String created;
    private String modified;
}