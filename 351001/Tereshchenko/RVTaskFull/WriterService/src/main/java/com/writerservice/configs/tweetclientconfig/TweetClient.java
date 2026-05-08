package com.writerservice.configs.tweetclientconfig;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/api/v1.0")
public interface TweetClient {

    @DeleteExchange("/tweets/by-writer/{writerId}")
    void deleteTweetsByWriterId(@PathVariable Long writerId);
}
