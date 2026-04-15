package com.messageservice.dtos;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MessageRequestTo {

    private Long tweetId;

    @Size(min = 2, max = 2048)
    private String content;
}
