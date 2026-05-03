package by.bsuir.task310.dto;

import lombok.Data;

@Data
public class ReactionRequestTo {
    private Long id;
    private Long topicId;
    private String content;
}