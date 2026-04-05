package com.tweetservice.dtos.tweetmarkers;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TweetMarkersRequestTo {

    private Long tweetId;

    private Long markerId;
}
