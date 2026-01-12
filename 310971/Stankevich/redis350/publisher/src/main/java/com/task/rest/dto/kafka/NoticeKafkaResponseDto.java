package com.task.rest.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeKafkaResponseDto {
    private String requestId;
    private String operation;
    private Long id;
    private Long tweetId;
    private String content;
    private String country;
    private String state;
    private String error;
    private List<NoticeKafkaRequestDto> notices;
}