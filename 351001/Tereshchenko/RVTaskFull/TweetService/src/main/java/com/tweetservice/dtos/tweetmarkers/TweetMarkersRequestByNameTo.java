package com.tweetservice.dtos.tweetmarkers;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TweetMarkersRequestByNameTo {

    private Long tweetId;

    private String markerName;
}
