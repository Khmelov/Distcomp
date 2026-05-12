package by.bsuir.distcomp.client;

import by.bsuir.distcomp.core.exception.ResourceNotFoundException;
import by.bsuir.distcomp.dto.request.ReactionRequestTo;
import by.bsuir.distcomp.dto.response.ReactionResponseTo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@ConditionalOnProperty(name = "discussion.mock", havingValue = "false", matchIfMissing = true)
public class RestDiscussionReactionClient implements DiscussionReactionClient {

    private final RestClient restClient;

    public RestDiscussionReactionClient(@Qualifier("discussionRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public ReactionResponseTo create(ReactionRequestTo dto) {
        return restClient.post()
                .uri("/api/v1.0/reactions")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .body(ReactionResponseTo.class);
    }

    @Override
    public ReactionResponseTo getById(Long id) {
        return restClient.get()
                .uri("/api/v1.0/reactions/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.value() == 404, (req, res) -> {
                    throw new ResourceNotFoundException("Reaction not found", 40417);
                })
                .body(ReactionResponseTo.class);
    }

    @Override
    public List<ReactionResponseTo> getAll() {
        List<ReactionResponseTo> list = restClient.get()
                .uri("/api/v1.0/reactions")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ReactionResponseTo>>() {});
        return list != null ? list : List.of();
    }

    @Override
    public List<ReactionResponseTo> getByTweetId(Long tweetId) {
        List<ReactionResponseTo> list = restClient.get()
                .uri("/api/v1.0/reactions/tweet/{tweetId}", tweetId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ReactionResponseTo>>() {});
        return list != null ? list : List.of();
    }

    @Override
    public ReactionResponseTo update(ReactionRequestTo dto) {
        return restClient.put()
                .uri("/api/v1.0/reactions")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .onStatus(status -> status.value() == 404, (req, res) -> {
                    throw new ResourceNotFoundException("Reaction not found", 40418);
                })
                .body(ReactionResponseTo.class);
    }

    @Override
    public void deleteById(Long id) {
        restClient.delete()
                .uri("/api/v1.0/reactions/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.value() == 404, (req, res) -> {
                    throw new ResourceNotFoundException("Reaction not found", 40420);
                })
                .toBodilessEntity();
    }

    @Override
    public void deleteByTweetId(Long tweetId) {
        restClient.delete()
                .uri("/api/v1.0/reactions/tweet/{tweetId}", tweetId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toBodilessEntity();
    }
}
