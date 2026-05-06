package com.example.task320.domain.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReactionResponseTo {
    private Long id;
    private Long tweetId;
    private String content;
}