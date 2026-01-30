package com.sergey.orsik.dto.request;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonRootName("comment")
public class CommentRequestTo {

    private Long id;
    private Long tweetId;
    private String content;
    private Instant created;
}
