package com.tweetservice.configs.tweetmarkersclientconfig;

import com.tweetservice.dtos.tweetmarkers.TweetMarkersRequestByNameTo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;

@HttpExchange("/api/v1.0")
public interface TweetMarkersClient {

    @GetExchange("/tweet-markers/{tweetId}")
    List<Long> getMarkersByTweetId(@PathVariable Long tweetId);

    @PostExchange("/tweet-markers-all")
    void linkTweetMarkers(@RequestBody List<TweetMarkersRequestByNameTo> request);

    @DeleteExchange("/tweet-markers/{tweetId}")
    List<Long> unlinkByTweetId(@PathVariable Long tweetId);
}
