package com.messageservice.configs.tweetclientconfig;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.Map;

@HttpExchange("/api/v1.0")
public interface TweetClient {

    @GetExchange("/tweets/{id}")
    Map<String, Object> getTweetById(@PathVariable Long id);
}
