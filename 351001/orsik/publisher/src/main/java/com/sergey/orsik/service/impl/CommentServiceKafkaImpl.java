package com.sergey.orsik.service.impl;

import com.sergey.orsik.dto.CommentState;
import com.sergey.orsik.dto.kafka.CommentTransportOperation;
import com.sergey.orsik.dto.kafka.CommentTransportReply;
import com.sergey.orsik.dto.kafka.CommentTransportRequest;
import com.sergey.orsik.dto.request.CommentRequestTo;
import com.sergey.orsik.dto.response.CommentResponseTo;
import com.sergey.orsik.exception.EntityNotFoundException;
import com.sergey.orsik.kafka.CommentReplyWaitRegistry;
import com.sergey.orsik.repository.TweetRepository;
import com.sergey.orsik.service.CommentService;
import com.sergey.orsik.util.CommentIds;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@Primary
public class CommentServiceKafkaImpl implements CommentService {

    private static final long RPC_TIMEOUT_MS = 1000L;

    private final KafkaTemplate<String, CommentTransportRequest> kafkaTemplate;
    private final CommentReplyWaitRegistry replyWaitRegistry;
    private final TweetRepository tweetRepository;
    private final String inTopic;

    public CommentServiceKafkaImpl(
            KafkaTemplate<String, CommentTransportRequest> kafkaTemplate,
            CommentReplyWaitRegistry replyWaitRegistry,
            TweetRepository tweetRepository,
            @Value("${kafka.topic.in:InTopic}") String inTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.replyWaitRegistry = replyWaitRegistry;
        this.tweetRepository = tweetRepository;
        this.inTopic = inTopic;
    }

    @Override
    public List<CommentResponseTo> findAll(int page, int size, String sortBy, String sortDir, Long tweetId, String content) {
        String correlationId = UUID.randomUUID().toString();
        CommentTransportRequest request = new CommentTransportRequest();
        request.setCorrelationId(correlationId);
        request.setOperation(CommentTransportOperation.FIND_ALL);
        request.setPage(page);
        request.setSize(size);
        request.setSortBy(sortBy);
        request.setSortDir(sortDir);
        request.setTweetId(tweetId);
        request.setContent(content);

        CommentTransportReply reply = sendAndAwait(request, partitionKeyForTweetOrZero(tweetId));
        assertOk(reply);
        return reply.getComments() != null ? reply.getComments() : List.of();
    }

    @Override
    public CommentResponseTo findById(Long id) {
        String correlationId = UUID.randomUUID().toString();
        CommentTransportRequest request = new CommentTransportRequest();
        request.setCorrelationId(correlationId);
        request.setOperation(CommentTransportOperation.GET_BY_ID);
        request.setCommentId(id);

        CommentTransportReply reply = sendAndAwait(request, String.valueOf(id));
        assertOk(reply);
        return reply.getComment();
    }

    @Override
    public CommentResponseTo create(CommentRequestTo request) {
        tweetRepository.findById(request.getTweetId())
                .orElseThrow(() -> new EntityNotFoundException("Tweet", request.getTweetId()));

        long id = CommentIds.newId();
        Instant created = request.getCreated() != null ? request.getCreated() : Instant.now();

        CommentRequestTo body = new CommentRequestTo(id, request.getTweetId(), request.getContent(), created);

        CommentTransportRequest transport = new CommentTransportRequest();
        transport.setOperation(CommentTransportOperation.CREATE_ASYNC);
        transport.setBody(body);

        kafkaTemplate.send(inTopic, String.valueOf(request.getTweetId()), transport);

        return new CommentResponseTo(id, request.getTweetId(), request.getContent(), created, CommentState.PENDING);
    }

    @Override
    public CommentResponseTo update(Long id, CommentRequestTo request) {
        String correlationId = UUID.randomUUID().toString();
        CommentTransportRequest transport = new CommentTransportRequest();
        transport.setCorrelationId(correlationId);
        transport.setOperation(CommentTransportOperation.UPDATE);
        transport.setCommentId(id);
        transport.setBody(request);

        CommentTransportReply reply = sendAndAwait(transport, String.valueOf(request.getTweetId()));
        assertOk(reply);
        return reply.getComment();
    }

    @Override
    public void deleteById(Long id) {
        String correlationId = UUID.randomUUID().toString();
        CommentTransportRequest transport = new CommentTransportRequest();
        transport.setCorrelationId(correlationId);
        transport.setOperation(CommentTransportOperation.DELETE_BY_ID);
        transport.setCommentId(id);

        CommentTransportReply reply = sendAndAwait(transport, String.valueOf(id));
        assertOk(reply);
    }

    private CommentTransportReply sendAndAwait(CommentTransportRequest request, String partitionKey) {
        String correlationId = request.getCorrelationId();
        if (correlationId == null) {
            throw new IllegalStateException("correlationId required for RPC");
        }
        var future = replyWaitRegistry.register(correlationId);
        try {
            kafkaTemplate.send(inTopic, partitionKey, request).get(5, TimeUnit.SECONDS);
        } catch (TimeoutException | ExecutionException e) {
            replyWaitRegistry.discard(correlationId);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "failed to send Kafka request", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            replyWaitRegistry.discard(correlationId);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "interrupted", e);
        }
        try {
            return future.get(RPC_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            replyWaitRegistry.discard(correlationId);
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "discussion did not respond in time");
        } catch (ExecutionException e) {
            replyWaitRegistry.discard(correlationId);
            Throwable c = e.getCause() != null ? e.getCause() : e;
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, c.getMessage(), c);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            replyWaitRegistry.discard(correlationId);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "interrupted", e);
        }
    }

    private static String partitionKeyForTweetOrZero(Long tweetId) {
        return tweetId != null ? String.valueOf(tweetId) : "0";
    }

    private void assertOk(CommentTransportReply reply) {
        if (reply == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "empty reply from discussion");
        }
        if (reply.isError()) {
            throw mapTransportError(reply);
        }
    }

    private RuntimeException mapTransportError(CommentTransportReply reply) {
        String msg = reply.getErrorMessage() != null ? reply.getErrorMessage() : "error from discussion";
        if ("Tweet".equals(reply.getErrorEntityName()) && reply.getErrorEntityId() != null) {
            return new EntityNotFoundException("Tweet", reply.getErrorEntityId());
        }
        if ("Comment".equals(reply.getErrorEntityName()) && reply.getErrorEntityId() != null) {
            return new EntityNotFoundException("Comment", reply.getErrorEntityId());
        }
        if (msg.toLowerCase().contains("tweet") && reply.getErrorEntityId() != null) {
            return new EntityNotFoundException("Tweet", reply.getErrorEntityId());
        }
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }
}
