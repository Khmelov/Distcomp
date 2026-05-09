package com.example.discussion.dto.response;
import com.example.discussion.entity.MessageState;
import lombok.Data;

@Data
public class MessageResponseTo {
    private Long id;
    private Long storyId;
    private String content;
    private MessageState state;
}