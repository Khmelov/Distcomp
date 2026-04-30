package com.tweetservice.dtos.message;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageResponseTo {

    private Long id;

    private Long tweetId;

    private String content;

    private String state;
}
