package com.task.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeResponseTo {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("tweetId")
    private Long tweetId;

    @JsonProperty("content")
    private String content;
}