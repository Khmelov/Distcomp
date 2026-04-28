package com.example.task350.domain.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReactionMessage {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("tweetId")
    private Long tweetId;
    
    @JsonProperty("content")
    private String content;
    
    @JsonProperty("state")
    private String state;
    
    @JsonProperty("country")
    private String country;
}
