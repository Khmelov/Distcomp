package com.messageservice.dtos;

import com.messageservice.models.MessageState;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageResponseTo {

    private Long id;

    private Long tweetId;

    private String content;

    private MessageState state;
}
