package com.tweetservice.dtos.tweetmarkers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TweetMarkersResponseTo {

    private Long tweetId;

    private Long markerId;

}
