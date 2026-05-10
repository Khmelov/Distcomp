package by.bsuir.distcomp.discussion.client;

import by.bsuir.distcomp.discussion.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PublisherTweetClient {

    private final RestClient publisherRestClient;

    public PublisherTweetClient(RestClient publisherRestClient) {
        this.publisherRestClient = publisherRestClient;
    }

    public void requireTweetExists(Long tweetId) {
        publisherRestClient.get()
                .uri("/api/v1.0/tweets/{id}", tweetId)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        (req, res) -> {
                            throw new ResourceNotFoundException(
                                    "Tweet with id " + tweetId + " not found", 40412);
                        })
                .toBodilessEntity();
    }
}
