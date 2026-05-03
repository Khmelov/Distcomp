package by.bsuir.task310.model;

import lombok.Data;

@Data
public class Reaction {
    private Long id;
    private Long topicId;
    private String content;
}