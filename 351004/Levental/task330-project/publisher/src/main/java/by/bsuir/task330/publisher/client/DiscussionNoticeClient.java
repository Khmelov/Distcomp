package by.bsuir.task330.publisher.client;

import by.bsuir.task330.publisher.dto.NoticeRequestTo;
import by.bsuir.task330.publisher.dto.NoticeResponseTo;
import by.bsuir.task330.publisher.error.RemoteServiceException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Component
public class DiscussionNoticeClient {

    private final RestClient restClient;

    public DiscussionNoticeClient(RestClient discussionRestClient) {
        this.restClient = discussionRestClient;
    }

    public NoticeResponseTo create(NoticeRequestTo request) {
        return restClient.post()
                .uri("/api/v1.0/notices")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, resp) -> {
                    throw new RemoteServiceException(
                            "Discussion service error: " + resp.getStatusCode().value(),
                            resp.getStatusCode().value()
                    );
                })
                .body(NoticeResponseTo.class);
    }

    public NoticeResponseTo update(NoticeRequestTo request) {
        return restClient.put()
                .uri("/api/v1.0/notices")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, resp) -> {
                    throw new RemoteServiceException(
                            "Discussion service error: " + resp.getStatusCode().value(),
                            resp.getStatusCode().value()
                    );
                })
                .body(NoticeResponseTo.class);
    }

    public NoticeResponseTo findById(Long id) {
        return restClient.get()
                .uri("/api/v1.0/notices/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, resp) -> {
                    throw new RemoteServiceException(
                            "Discussion service error: " + resp.getStatusCode().value(),
                            resp.getStatusCode().value()
                    );
                })
                .body(NoticeResponseTo.class);
    }

    public List<NoticeResponseTo> findAll(Integer page, Integer size, String sort, String filter, Long articleId) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1.0/notices")
                        .queryParamIfPresent("page", Optional.ofNullable(page))
                        .queryParamIfPresent("size", Optional.ofNullable(size))
                        .queryParamIfPresent("sort", Optional.ofNullable(sort))
                        .queryParamIfPresent("filter", Optional.ofNullable(filter))
                        .queryParamIfPresent("articleId", Optional.ofNullable(articleId))
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, resp) -> {
                    throw new RemoteServiceException(
                            "Discussion service error: " + resp.getStatusCode().value(),
                            resp.getStatusCode().value()
                    );
                })
                .body(new ParameterizedTypeReference<List<NoticeResponseTo>>() {});
    }

    public void delete(Long id) {
        restClient.delete()
                .uri("/api/v1.0/notices/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, resp) -> {
                    throw new RemoteServiceException(
                            "Discussion service error: " + resp.getStatusCode().value(),
                            resp.getStatusCode().value()
                    );
                })
                .toBodilessEntity();
    }
}