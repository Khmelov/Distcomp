package com.task.discussion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeResponseTo {
    private Long id;
    private String country;
    private Long tweetId;
    private String content;
}