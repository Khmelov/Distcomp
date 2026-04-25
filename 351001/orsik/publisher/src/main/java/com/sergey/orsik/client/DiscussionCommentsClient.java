package com.sergey.orsik.client;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class DiscussionCommentsClient {

    private static final String BY_TWEET_PATH = "/api/v1.0/comments/by-tweet/{tweetId}";

    private final RestClient discussionRestClient;

    public DiscussionCommentsClient(RestClient discussionRestClient) {
        this.discussionRestClient = discussionRestClient;
    }

    /**
     * Removes all comments for a tweet in the discussion service. Idempotent if none exist.
     */
    public void deleteAllForTweet(Long tweetId) {
        try {
            discussionRestClient.delete()
                    .uri(BY_TWEET_PATH, tweetId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return;
            }
            throw ex;
        }
    }
}
