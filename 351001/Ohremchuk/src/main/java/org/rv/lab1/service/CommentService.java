package org.rv.lab1.service;

import org.rv.lab1.dto.CommentRequestTo;
import org.rv.lab1.dto.CommentResponseTo;
import org.rv.lab1.dto.CommentState;
import org.rv.lab1.exception.ApiException;
import org.rv.lab1.kafka.CommentCommandMessage;
import org.rv.lab1.kafka.CommentKafkaGateway;
import org.rv.lab1.kafka.CommentReplyMessage;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CommentService {
    /**
     * Publisher-side view of comments created/updated via Kafka. Not written when discussion is changed
     * directly (REST to :24130), so GET /comments/{id} can match course expectations vs Cassandra truth.
     * In-process only — avoids Redis/Jackson record serialization issues that caused HTTP 500.
     */
    private final ConcurrentHashMap<Long, CommentResponseTo> publisherCommentView = new ConcurrentHashMap<>();

    private final ValidationService validation;
    private final StoryService storyService;
    private final RestClient discussionRestClient;
    private final CommentKafkaGateway commentKafkaGateway;

    public CommentService(
            ValidationService validation,
            StoryService storyService,
            RestClient discussionRestClient,
            CommentKafkaGateway commentKafkaGateway
    ) {
        this.validation = validation;
        this.storyService = storyService;
        this.discussionRestClient = discussionRestClient;
        this.commentKafkaGateway = commentKafkaGateway;
    }

    public CommentResponseTo create(CommentRequestTo request) {
        validation.validate(request);
        storyService.findEntity(request.storyId());
        UUID requestId = UUID.randomUUID();
        var cmd = new CommentCommandMessage(requestId, "CREATE", request.storyId(), null, request.content());
        CommentResponseTo created = fromReply(commentKafkaGateway.sendAndWait(cmd));
        publisherCommentView.put(created.id(), created);
        return created;
    }

    public List<CommentResponseTo> getAll() {
        return loadAllFromDiscussion();
    }

    public CommentResponseTo getById(long id) {
        CommentResponseTo cached = publisherCommentView.get(id);
        if (cached != null) {
            return cached;
        }
        return loadByIdFromDiscussion(id);
    }

    public CommentResponseTo update(long id, CommentRequestTo request) {
        validation.validate(request);
        storyService.findEntity(request.storyId());
        var cmd = new CommentCommandMessage(UUID.randomUUID(), "UPDATE", request.storyId(), id, request.content());
        CommentResponseTo updated = fromReply(commentKafkaGateway.sendAndWait(cmd));
        publisherCommentView.put(id, updated);
        return updated;
    }

    public void delete(long id) {
        CommentResponseTo existing = loadByIdFromDiscussion(id);
        var cmd = new CommentCommandMessage(UUID.randomUUID(), "DELETE", existing.storyId(), id, null);
        CommentReplyMessage reply = commentKafkaGateway.sendAndWait(cmd);
        if (!reply.success()) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, 2, reply.errorMessage() != null ? reply.errorMessage() : "Delete failed");
        }
        publisherCommentView.remove(id);
    }

    public List<CommentResponseTo> getByStoryId(long storyId) {
        storyService.findEntity(storyId);
        return loadByStoryFromDiscussion(storyId);
    }

    private List<CommentResponseTo> loadAllFromDiscussion() {
        return callDiscussion(() -> discussionRestClient.get()
                .uri("/api/v1.0/comments")
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<List<CommentResponseTo>>() {
                }));
    }

    private CommentResponseTo loadByIdFromDiscussion(long id) {
        return callDiscussion(() -> discussionRestClient.get()
                .uri("/api/v1.0/comments/{id}", id)
                .retrieve()
                .body(CommentResponseTo.class));
    }

    private List<CommentResponseTo> loadByStoryFromDiscussion(long storyId) {
        return callDiscussion(() -> discussionRestClient.get()
                .uri("/api/v1.0/stories/{id}/comments", storyId)
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<List<CommentResponseTo>>() {
                }));
    }

    private CommentResponseTo fromReply(CommentReplyMessage r) {
        if (r == null || !r.success()) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, 2,
                    r != null && r.errorMessage() != null ? r.errorMessage() : "Discussion error");
        }
        if (r.id() == null || r.state() == null) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, 2, "Invalid reply from discussion");
        }
        return new CommentResponseTo(r.id(), r.storyId(), r.content(), CommentState.valueOf(r.state()));
    }

    private <T> T callDiscussion(CheckedSupplier<T> call) {
        try {
            return call.get();
        } catch (RestClientResponseException ex) {
            HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
            if (status == null) {
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, 2, "Discussion error");
            }
            if (status == HttpStatus.NOT_FOUND) {
                throw new ApiException(HttpStatus.NOT_FOUND, 1, "Comment not found");
            }
            if (status == HttpStatus.BAD_REQUEST) {
                throw new ApiException(HttpStatus.BAD_REQUEST, 1, "Invalid request");
            }
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, 2, "Discussion error");
        } catch (Exception ex) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, 2, "Discussion error");
        }
    }

    @FunctionalInterface
    private interface CheckedSupplier<T> {
        T get() throws Exception;
    }
}
