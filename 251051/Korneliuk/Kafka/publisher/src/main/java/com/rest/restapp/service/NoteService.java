package com.rest.restapp.service;

import com.common.KafkaOperation;
import com.common.NoteAsyncResponse;
import com.common.NoteMessage;
import com.common.NoteResponseTo;
import com.common.NoteState;
import com.rest.restapp.client.DiscussionClient;
import com.rest.restapp.dto.request.NoteRequestTo;
import com.rest.restapp.exception.NotFoundException;
import com.rest.restapp.exception.ValidationException;
import com.rest.restapp.mapper.NoteMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class NoteService {

    DiscussionClient discussionClient;
    StreamBridge streamBridge;
    CompositeIdCodecService codecService;
    NoteMapper mapper;

    private final Map<String, CompletableFuture<List<NoteResponseTo>>> pending = new ConcurrentHashMap<>();

    public NoteResponseTo createNote(NoteRequestTo requestTo) {
        validateNoteRequest(requestTo);

        var key = codecService.encode("RU", requestTo.issueId(), generateId());

        var message = NoteMessage
                .builder()
                .operation(KafkaOperation.CREATE)
                .state(NoteState.PENDING)
                .id(key)
                .correlationId(UUID.randomUUID().toString())
                .issueId(requestTo.issueId())
                .content(requestTo.content())
                .build();

        sendToDiscussion(message);
        return mapper.mapToResponse(message);
    }

    public NoteResponseTo getNoteById(Long id) {
        var correlationId = UUID.randomUUID().toString();
        var future = new CompletableFuture<List<NoteResponseTo>>();
        var message = NoteMessage
                .builder()
                .operation(KafkaOperation.GET)
                .id(id)
                .correlationId(correlationId)
                .build();
        sendToDiscussion(message);
        pending.put(correlationId, future);

        try {
            return future
                    .get(2, TimeUnit.SECONDS)
                    .stream()
                    .findFirst()
                    .orElseThrow();
        } catch (Exception e) {
            pending.remove(correlationId);
            throw new NotFoundException("Failed to fetch note or not found note by id" + id.toString());
        }
    }

    public List<NoteResponseTo> getAllNotes() {
        var correlationId = UUID.randomUUID().toString();
        var future = new CompletableFuture<List<NoteResponseTo>>();
        var message = NoteMessage
                .builder()
                .operation(KafkaOperation.GET_ALL)
                .correlationId(correlationId)
                .build();
        sendToDiscussion(message);
        pending.put(correlationId, future);

        try {
            return future.get(2, TimeUnit.SECONDS);
        } catch (Exception e) {
            pending.remove(correlationId);
            throw new NotFoundException("Failed to fetch notes or not found note by id");
        }
    }

    public NoteResponseTo updateNote(NoteRequestTo requestTo) {
        validateNoteRequest(requestTo);
        var correlationId = UUID.randomUUID().toString();
        var future = new CompletableFuture<List<NoteResponseTo>>();
        var message = NoteMessage
                .builder()
                .operation(KafkaOperation.UPDATE)
                .state(NoteState.PENDING)
                .id(requestTo.id())
                .correlationId(correlationId)
                .issueId(requestTo.issueId())
                .content(requestTo.content())
                .build();

        sendToDiscussion(message);
        pending.put(correlationId, future);
        try {
            return future
                    .get(2, TimeUnit.SECONDS)
                    .stream()
                    .findFirst()
                    .orElseThrow();
        } catch (Exception e) {
            pending.remove(correlationId);
            throw new NotFoundException("Failed to fetch notes or not found note by id");
        }
    }

    public void deleteNote(Long id) {
        var message = NoteMessage
                .builder()
                .operation(KafkaOperation.DELETE)
                .id(id)
                .correlationId(UUID.randomUUID().toString())
                .build();
        sendToDiscussion(message);
    }

    @Transactional(readOnly = true)
    public List<NoteResponseTo> getNotesByIssueId(Long issueId) {
        return discussionClient.getNotesByIssueId(issueId);
    }

    private void validateNoteRequest(NoteRequestTo requestTo) {
        if (requestTo == null) {
            throw new ValidationException("Note request cannot be null");
        }
        if (requestTo.issueId() == null) {
            throw new ValidationException("Issue ID is required");
        }
        if (requestTo.content() == null || requestTo.content().trim().isEmpty()) {
            throw new ValidationException("Content is required");
        }
    }

    public void handleResponse(NoteAsyncResponse message) {
        var future = pending.remove(message.correlationId());
        if (future != null) {
            future.complete(message.noteMessages());
        }
    }

    private void sendToDiscussion(NoteMessage message) {
        streamBridge.send("to-discussion-out-0", message);
    }

    private long generateId() {
        return Math.abs(UUID.randomUUID ().getMostSignificantBits());
    }
}
