package com.sergey.orsik.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseTo {

    private Long id;
    private Long tweetId;
    private String content;
    private Instant created;
}
