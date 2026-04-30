package by.bsuir.distcomp.discussion.kafka;

import by.bsuir.distcomp.discussion.cassandra.ReactionCassandraRepository;
import by.bsuir.distcomp.discussion.cassandra.ReactionRow;
import by.bsuir.distcomp.discussion.client.PublisherTweetClient;
import by.bsuir.distcomp.discussion.exception.ResourceNotFoundException;
import by.bsuir.distcomp.discussion.model.ReactionState;
import by.bsuir.distcomp.discussion.service.ModerationService;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class ReactionInKafkaHandler {

    public static final String OP_CREATE = "CREATE";
    public static final String OP_GET_BY_ID = "GET_BY_ID";
    public static final String OP_GET_ALL = "GET_ALL";
    public static final String OP_UPDATE = "UPDATE";
    public static final String OP_DELETE = "DELETE";

    private final ReactionCassandraRepository reactionRepository;
    private final PublisherTweetClient publisherTweetClient;
    private final ModerationService moderationService;

    public ReactionInKafkaHandler(ReactionCassandraRepository reactionRepository,
                                  PublisherTweetClient publisherTweetClient,
                                  ModerationService moderationService) {
        this.reactionRepository = reactionRepository;
        this.publisherTweetClient = publisherTweetClient;
        this.moderationService = moderationService;
    }

    public void handleCreate(ReactionInMessage msg) {
        ReactionSnapshot snap = msg.getSnapshot();
        if (snap == null || snap.getTweetId() == null || snap.getId() == null) {
            return;
        }
        publisherTweetClient.requireTweetExists(snap.getTweetId());
        ReactionState state = moderationService.moderate(snap.getContent());
        ReactionRow row = new ReactionRow(snap.getId(), snap.getTweetId(), snap.getContent(), state);
        reactionRepository.save(row);
    }

    public ReactionOutMessage handleSync(ReactionInMessage msg) {
        String cid = msg.getCorrelationId();
        try {
            return switch (msg.getOperation()) {
                case OP_GET_BY_ID -> handleGetById(cid, msg.getReactionId());
                case OP_GET_ALL -> handleGetAll(cid);
                case OP_UPDATE -> handleUpdate(cid, msg.getSnapshot());
                case OP_DELETE -> handleDelete(cid, msg.getReactionId());
                default -> error(cid, 400, "Unknown operation");
            };
        } catch (ResourceNotFoundException e) {
            return error(cid, 404, e.getMessage());
        } catch (Exception e) {
            return error(cid, 500, e.getMessage());
        }
    }

    private ReactionOutMessage handleGetById(String correlationId, Long id) {
        ReactionOutMessage out = new ReactionOutMessage();
        out.setCorrelationId(correlationId);
        ReactionRow row = reactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reaction with id " + id + " not found", 40413));
        out.setStatus(200);
        out.setSnapshot(toSnapshot(row));
        return out;
    }

    private ReactionOutMessage handleGetAll(String correlationId) {
        ReactionOutMessage out = new ReactionOutMessage();
        out.setCorrelationId(correlationId);
        out.setStatus(200);
        out.setSnapshots(reactionRepository.findAll().stream().map(this::toSnapshot).collect(Collectors.toList()));
        return out;
    }

    private ReactionOutMessage handleUpdate(String correlationId, ReactionSnapshot snap) {
        if (snap == null || snap.getId() == null || snap.getTweetId() == null) {
            return error(correlationId, 400, "Invalid snapshot");
        }
        reactionRepository.findById(snap.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reaction with id " + snap.getId() + " not found", 40414));
        publisherTweetClient.requireTweetExists(snap.getTweetId());
        ReactionState state = moderationService.moderate(snap.getContent());
        ReactionRow updated = new ReactionRow(snap.getId(), snap.getTweetId(), snap.getContent(), state);
        reactionRepository.save(updated);
        ReactionOutMessage out = new ReactionOutMessage();
        out.setCorrelationId(correlationId);
        out.setStatus(200);
        out.setSnapshot(toSnapshot(updated));
        return out;
    }

    private ReactionOutMessage handleDelete(String correlationId, Long id) {
        if (!reactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reaction with id " + id + " not found", 40416);
        }
        reactionRepository.deleteById(id);
        ReactionOutMessage out = new ReactionOutMessage();
        out.setCorrelationId(correlationId);
        out.setStatus(204);
        return out;
    }

    private static ReactionOutMessage error(String correlationId, int status, String message) {
        ReactionOutMessage out = new ReactionOutMessage();
        out.setCorrelationId(correlationId);
        out.setStatus(status);
        out.setErrorMessage(message);
        return out;
    }

    private ReactionSnapshot toSnapshot(ReactionRow row) {
        return new ReactionSnapshot(row.getId(), row.getTweetId(), row.getContent(), row.getState());
    }
}
