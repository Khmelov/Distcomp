package com.task.discussion.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeKafkaRequestDto {
    private String requestId;
    private String operation;
    private Long id;
    private Long tweetId;
    private String country;
    private String content;
    private String state;
}