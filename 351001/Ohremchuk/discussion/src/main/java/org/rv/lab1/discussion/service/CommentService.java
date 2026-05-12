package org.rv.lab1.discussion.service;

import org.rv.lab1.discussion.domain.CommentById;
import org.rv.lab1.discussion.domain.CommentByStory;
import org.rv.lab1.discussion.domain.CommentByStoryKey;
import org.rv.lab1.discussion.domain.CommentState;
import org.rv.lab1.discussion.dto.CommentRequestTo;
import org.rv.lab1.discussion.dto.CommentResponseTo;
import org.rv.lab1.discussion.exception.ApiException;
import org.rv.lab1.discussion.kafka.CommentCommandMessage;
import org.rv.lab1.discussion.kafka.CommentReplyMessage;
import org.rv.lab1.discussion.repo.CommentByIdRepository;
import org.rv.lab1.discussion.repo.CommentByStoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    private final CommentByIdRepository byIdRepository;
    private final CommentByStoryRepository byStoryRepository;
    private final CommentModerationService moderation;

    public CommentService(
            CommentByIdRepository byIdRepository,
            CommentByStoryRepository byStoryRepository,
            CommentModerationService moderation
    ) {
        this.byIdRepository = byIdRepository;
        this.byStoryRepository = byStoryRepository;
        this.moderation = moderation;
    }

    public CommentResponseTo create(CommentRequestTo request) {
        long id = IdGenerator.nextId();
        long storyId = request.storyId();
        CommentState state = moderation.moderate(request.content());

        byIdRepository.save(new CommentById(id, storyId, request.content(), state));
        byStoryRepository.save(new CommentByStory(new CommentByStoryKey(storyId, id), request.content(), state));

        return new CommentResponseTo(id, storyId, request.content(), state);
    }

    public List<CommentResponseTo> getAll() {
        return byIdRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public CommentResponseTo getById(long id) {
        var c = byIdRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, 1, "Comment not found: " + id));
        return toResponse(c);
    }

    public List<CommentResponseTo> getByStoryId(long storyId) {
        return byStoryRepository.findAllByKeyStoryId(storyId).stream()
                .map(c -> new CommentResponseTo(
                        c.getKey().getId(),
                        c.getKey().getStoryId(),
                        c.getContent(),
                        stateOrDefault(c.getStateEnum())))
                .toList();
    }

    public CommentResponseTo update(long id, CommentRequestTo request) {
        var existing = byIdRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, 1, "Comment not found: " + id));

        long storyId = request.storyId();
        CommentState state = moderation.moderate(request.content());

        if (!existing.getStoryId().equals(storyId)) {
            byStoryRepository.deleteById(new CommentByStoryKey(existing.getStoryId(), id));
        }

        existing.setStoryId(storyId);
        existing.setContent(request.content());
        existing.setStateEnum(state);
        byIdRepository.save(existing);
        byStoryRepository.save(new CommentByStory(new CommentByStoryKey(storyId, id), request.content(), state));

        return new CommentResponseTo(id, storyId, request.content(), state);
    }

    public void delete(long id) {
        var existing = byIdRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, 1, "Comment not found: " + id));
        byIdRepository.deleteById(id);
        byStoryRepository.deleteById(new CommentByStoryKey(existing.getStoryId(), id));
    }

    /**
     * Handles commands from Kafka {@code InTopic}; responds via {@code OutTopic}.
     */
    public CommentReplyMessage processKafkaCommand(CommentCommandMessage cmd) {
        if (cmd == null || cmd.requestId() == null || cmd.operation() == null) {
            return CommentReplyMessage.fail(
                    cmd != null ? cmd.requestId() : null,
                    "Invalid command");
        }
        try {
            return switch (cmd.operation()) {
                case "CREATE" -> createFromCommand(cmd);
                case "UPDATE" -> updateFromCommand(cmd);
                case "DELETE" -> deleteFromCommand(cmd);
                default -> CommentReplyMessage.fail(cmd.requestId(), "Unknown operation: " + cmd.operation());
            };
        } catch (ApiException ex) {
            return CommentReplyMessage.fail(cmd.requestId(), ex.getMessage());
        } catch (Exception ex) {
            return CommentReplyMessage.fail(cmd.requestId(), "Internal error");
        }
    }

    private CommentReplyMessage createFromCommand(CommentCommandMessage cmd) {
        if (cmd.storyId() == null || cmd.content() == null) {
            return CommentReplyMessage.fail(cmd.requestId(), "storyId and content required");
        }
        long id = IdGenerator.nextId();
        long storyId = cmd.storyId();
        CommentState state = moderation.moderate(cmd.content());
        byIdRepository.save(new CommentById(id, storyId, cmd.content(), state));
        byStoryRepository.save(new CommentByStory(new CommentByStoryKey(storyId, id), cmd.content(), state));
        return CommentReplyMessage.ok(cmd.requestId(), id, storyId, cmd.content(), state.name());
    }

    private CommentReplyMessage updateFromCommand(CommentCommandMessage cmd) {
        if (cmd.commentId() == null || cmd.storyId() == null || cmd.content() == null) {
            return CommentReplyMessage.fail(cmd.requestId(), "commentId, storyId and content required");
        }
        long id = cmd.commentId();
        CommentRequestTo req = new CommentRequestTo(cmd.storyId(), cmd.content());
        CommentResponseTo updated = update(id, req);
        return CommentReplyMessage.ok(cmd.requestId(), updated.id(), updated.storyId(), updated.content(), updated.state().name());
    }

    private CommentReplyMessage deleteFromCommand(CommentCommandMessage cmd) {
        if (cmd.commentId() == null) {
            return CommentReplyMessage.fail(cmd.requestId(), "commentId required");
        }
        delete(cmd.commentId());
        return new CommentReplyMessage(cmd.requestId(), true, null, cmd.commentId(), null, null, null);
    }

    private CommentResponseTo toResponse(CommentById c) {
        return new CommentResponseTo(c.getId(), c.getStoryId(), c.getContent(), stateOrDefault(c.getStateEnum()));
    }

    private CommentState stateOrDefault(CommentState s) {
        return s != null ? s : CommentState.APPROVE;
    }
}
