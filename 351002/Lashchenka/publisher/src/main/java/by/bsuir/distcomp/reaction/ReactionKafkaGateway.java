package by.bsuir.distcomp.reaction;

import by.bsuir.distcomp.dto.request.ReactionRequestTo;
import by.bsuir.distcomp.dto.response.ReactionResponseTo;
import by.bsuir.distcomp.exception.ResourceNotFoundException;
import by.bsuir.distcomp.kafka.ReactionInMessage;
import by.bsuir.distcomp.kafka.ReactionKafkaOps;
import by.bsuir.distcomp.kafka.ReactionSnapshot;
import by.bsuir.distcomp.model.ReactionState;
import by.bsuir.distcomp.repository.TweetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ReactionKafkaGateway {

    private final TweetRepository tweetRepository;
    private final ReactionInKafkaProducer inProducer;
    private final DiscussionReactionClient discussionReactionClient;

    public ReactionKafkaGateway(
            TweetRepository tweetRepository,
            ReactionInKafkaProducer inProducer,
            DiscussionReactionClient discussionReactionClient) {
        this.tweetRepository = tweetRepository;
        this.inProducer = inProducer;
        this.discussionReactionClient = discussionReactionClient;
    }

    public ResponseEntity<ReactionResponseTo> create(ReactionRequestTo dto) {
        if (!tweetRepository.existsById(dto.getTweetId())) {
            throw new ResourceNotFoundException("Tweet with id " + dto.getTweetId() + " not found", 40412);
        }
        long id = nextId();
        ReactionSnapshot snap = new ReactionSnapshot(id, dto.getTweetId(), dto.getContent(), ReactionState.PENDING);
        ReactionInMessage msg = new ReactionInMessage();
        msg.setOperation(ReactionKafkaOps.CREATE);
        msg.setTweetId(dto.getTweetId());
        msg.setSnapshot(snap);
        try {
            inProducer.send(msg, partitionKey(msg));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        ReactionResponseTo body = new ReactionResponseTo(id, dto.getTweetId(), dto.getContent(), ReactionState.PENDING);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    public ResponseEntity<ReactionResponseTo> getById(Long id) {
        return ResponseEntity.ok(discussionReactionClient.getById(id));
    }

    public ResponseEntity<List<ReactionResponseTo>> getAll() {
        return ResponseEntity.ok(discussionReactionClient.getAll());
    }

    public ResponseEntity<ReactionResponseTo> update(ReactionRequestTo dto) {
        return ResponseEntity.ok(discussionReactionClient.update(dto));
    }

    public ResponseEntity<Void> deleteById(Long id) {
        discussionReactionClient.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private static String partitionKey(ReactionInMessage msg) {
        return switch (msg.getOperation()) {
            case ReactionKafkaOps.CREATE -> String.valueOf(msg.getSnapshot().getTweetId());
            case ReactionKafkaOps.UPDATE -> String.valueOf(msg.getSnapshot().getTweetId());
            case ReactionKafkaOps.GET_BY_ID, ReactionKafkaOps.DELETE -> "id-" + msg.getReactionId();
            case ReactionKafkaOps.GET_ALL -> "__global__";
            default -> "__none__";
        };
    }

    private static long nextId() {
        return System.currentTimeMillis() * 10_000L + ThreadLocalRandom.current().nextInt(10_000);
    }
}
