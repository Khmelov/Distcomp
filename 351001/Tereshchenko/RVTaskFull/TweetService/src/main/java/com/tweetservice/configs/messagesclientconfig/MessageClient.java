package com.tweetservice.configs.messagesclientconfig;

import com.tweetservice.dtos.message.MessageResponseTo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange("/api/v1.0")
public interface MessageClient {

    @GetExchange("/messages/{tweetId}/tweet")
    List<MessageResponseTo> getAllMessagesByTweetId(@PathVariable Long tweetId);

    @DeleteExchange("/messages/tweets/{tweetId}")
    void deleteMessagesByTweetId(@PathVariable Long tweetId);
}
