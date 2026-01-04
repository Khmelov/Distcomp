package com.task.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeResponseTo {

    private Long id;
    private Long tweetId;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;
}