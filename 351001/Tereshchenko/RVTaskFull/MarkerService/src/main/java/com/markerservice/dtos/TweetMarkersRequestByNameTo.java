package com.markerservice.dtos;

import lombok.Data;

@Data
public class TweetMarkersRequestByNameTo {

    private Long tweetId;

    private String markerName;
}
