package by.bsuir.distcomp.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class DiscussionReactionsClient {

    private final WebClient discussionWebClient;
    private final String reactionsPath;

    public DiscussionReactionsClient(
            @Qualifier("discussionWebClient") WebClient discussionWebClient,
            @Value("${discussion.api.reactions-path:/api/v1.0/reactions}") String reactionsPath) {
        this.discussionWebClient = discussionWebClient;
        this.reactionsPath = reactionsPath;
    }

    public ResponseEntity<String> create(String jsonBody) {
        return discussionWebClient.post()
                .uri(reactionsPath)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jsonBody)
                .retrieve()
                .toEntity(String.class)
                .block();
    }

    public ResponseEntity<String> getById(Long id) {
        return discussionWebClient.get()
                .uri(reactionsPath + "/{id}", id)
                .retrieve()
                .toEntity(String.class)
                .block();
    }

    public ResponseEntity<String> getAll() {
        return discussionWebClient.get()
                .uri(reactionsPath)
                .retrieve()
                .toEntity(String.class)
                .block();
    }

    public ResponseEntity<String> update(String jsonBody) {
        return discussionWebClient.put()
                .uri(reactionsPath)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jsonBody)
                .retrieve()
                .toEntity(String.class)
                .block();
    }

    public ResponseEntity<Void> deleteById(Long id) {
        return discussionWebClient.delete()
                .uri(reactionsPath + "/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
