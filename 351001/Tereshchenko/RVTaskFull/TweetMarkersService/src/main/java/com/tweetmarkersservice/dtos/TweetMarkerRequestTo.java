package com.tweetmarkersservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TweetMarkerRequestTo {

    private Long tweetId;

    private Long markerId;
}
