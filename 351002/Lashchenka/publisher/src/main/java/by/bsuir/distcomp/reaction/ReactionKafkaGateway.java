package by.bsuir.distcomp.reaction;

import by.bsuir.distcomp.dto.request.ReactionRequestTo;
import by.bsuir.distcomp.dto.response.ReactionResponseTo;
import by.bsuir.distcomp.exception.ResourceNotFoundException;
import by.bsuir.distcomp.kafka.ReactionInMessage;
import by.bsuir.distcomp.kafka.ReactionKafkaOps;
import by.bsuir.distcomp.kafka.ReactionOutMessage;
import by.bsuir.distcomp.kafka.ReactionSnapshot;
import by.bsuir.distcomp.model.ReactionState;
import by.bsuir.distcomp.repository.TweetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
public class ReactionKafkaGateway {

    private static final long REPLY_TIMEOUT_MS = 1000L;

    private final TweetRepository tweetRepository;
    private final ReactionInKafkaProducer inProducer;
    private final ReactionReplyRegistry registry;

    public ReactionKafkaGateway(
            TweetRepository tweetRepository,
            ReactionInKafkaProducer inProducer,
            ReactionReplyRegistry registry) {
        this.tweetRepository = tweetRepository;
        this.inProducer = inProducer;
        this.registry = registry;
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
        String cid = UUID.randomUUID().toString();
        CompletableFuture<ReactionOutMessage> f = registry.prepare(cid);
        ReactionInMessage msg = new ReactionInMessage();
        msg.setCorrelationId(cid);
        msg.setOperation(ReactionKafkaOps.GET_BY_ID);
        msg.setReactionId(id);
        try {
            inProducer.send(msg, partitionKey(msg));
        } catch (Exception e) {
            registry.cancel(cid);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        ReactionOutMessage out = await(cid, f);
        return toSingleResponse(out);
    }

    public ResponseEntity<List<ReactionResponseTo>> getAll() {
        String cid = UUID.randomUUID().toString();
        CompletableFuture<ReactionOutMessage> f = registry.prepare(cid);
        ReactionInMessage msg = new ReactionInMessage();
        msg.setCorrelationId(cid);
        msg.setOperation(ReactionKafkaOps.GET_ALL);
        try {
            inProducer.send(msg, partitionKey(msg));
        } catch (Exception e) {
            registry.cancel(cid);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        ReactionOutMessage out = await(cid, f);
        if (out.getStatus() >= 400) {
            throw new ResponseStatusException(
                    HttpStatus.valueOf(out.getStatus()),
                    out.getErrorMessage() != null ? out.getErrorMessage() : "Error");
        }
        List<ReactionResponseTo> list = out.getSnapshots() == null
                ? List.of()
                : out.getSnapshots().stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    public ResponseEntity<ReactionResponseTo> update(ReactionRequestTo dto) {
        if (!tweetRepository.existsById(dto.getTweetId())) {
            throw new ResourceNotFoundException("Tweet with id " + dto.getTweetId() + " not found", 40407);
        }
        String cid = UUID.randomUUID().toString();
        CompletableFuture<ReactionOutMessage> f = registry.prepare(cid);
        ReactionSnapshot snap = new ReactionSnapshot(dto.getId(), dto.getTweetId(), dto.getContent(), null);
        ReactionInMessage msg = new ReactionInMessage();
        msg.setCorrelationId(cid);
        msg.setOperation(ReactionKafkaOps.UPDATE);
        msg.setTweetId(dto.getTweetId());
        msg.setSnapshot(snap);
        try {
            inProducer.send(msg, partitionKey(msg));
        } catch (Exception e) {
            registry.cancel(cid);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        ReactionOutMessage out = await(cid, f);
        return toSingleResponse(out);
    }

    public ResponseEntity<Void> deleteById(Long id) {
        String cid = UUID.randomUUID().toString();
        CompletableFuture<ReactionOutMessage> f = registry.prepare(cid);
        ReactionInMessage msg = new ReactionInMessage();
        msg.setCorrelationId(cid);
        msg.setOperation(ReactionKafkaOps.DELETE);
        msg.setReactionId(id);
        try {
            inProducer.send(msg, partitionKey(msg));
        } catch (Exception e) {
            registry.cancel(cid);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        ReactionOutMessage out = await(cid, f);
        if (out.getStatus() == 404) {
            throw new ResourceNotFoundException(out.getErrorMessage() != null ? out.getErrorMessage() : "Not found", 40416);
        }
        if (out.getStatus() >= 400) {
            throw new ResponseStatusException(HttpStatus.valueOf(out.getStatus()), out.getErrorMessage());
        }
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<ReactionResponseTo> toSingleResponse(ReactionOutMessage out) {
        if (out.getStatus() == 404) {
            throw new ResourceNotFoundException(out.getErrorMessage() != null ? out.getErrorMessage() : "Not found", 40413);
        }
        if (out.getStatus() >= 400) {
            throw new ResponseStatusException(HttpStatus.valueOf(out.getStatus()), out.getErrorMessage());
        }
        if (out.getSnapshot() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Empty reply");
        }
        return ResponseEntity.ok(toDto(out.getSnapshot()));
    }

    private ReactionOutMessage await(String correlationId, CompletableFuture<ReactionOutMessage> f) {
        try {
            return f.get(REPLY_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            registry.cancel(correlationId);
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "Discussion reply timeout");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            registry.cancel(correlationId);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (ExecutionException e) {
            registry.cancel(correlationId);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private ReactionResponseTo toDto(ReactionSnapshot s) {
        return new ReactionResponseTo(s.getId(), s.getTweetId(), s.getContent(), s.getState());
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
