package com.messageservice.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageResponseTo {

    private Long id;

    private Long tweetId;

    private String content;

}
